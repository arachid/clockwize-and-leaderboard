# Clockwise Matrix Algo & Leaderboard System Design

## Question 1: Clockwise Matrix Algo

We Wrote a function that, given a matrix of integers, builds a string with the entries of that matrix
appended in clockwise order.

    String arrayClockWise(int[][] matrix) {
    ...
    }

For instance, the 3x4 matrix below:
``
2, 3, 4, 8
5, 7, 9, 12
1, 0, 6, 10
``
We would make a string like this “2, 3, 4, 8, 12, 10, 6, 0, 1, 5, 7, 9”.

This problem can be solved iterativly and also recusivly. We decided to go with the recusive approach to make the code consise and easy to read. We simply traverse to array and when we hit a size bandory or a visited cell, we change direction following clock directions. When we can't move anymore, the recursion end.

**Time Complexity:** O(n) since we are traversing each element once

**Memory Complexity:** O(n) since we have to create a visited matrix

**Test coverage:** 100% with multiple scenarios: square, rectangle, one line, one column & empty matrix.

**Limitation:** For this exercise, we decided to support only rectangle and square matrix. But, we can extends this code to support other shapes or add validation on supported shapes.

## Question 2: Leaderboard System Design

Leaderboard are in almost any multiplayer video games. We have as task to design a leaderboard system for a video game compagnie that has more than 10 million concurrent players.

### Fonctionnal Requirements
- Leaderboards are based on the total number of eliminations across all games. Each elimination count for a score.
- There 4 supported game and each leaderboard support a game
- System should Have daily / weekly / all-time aggregations
- Have user driven filtering capabilities (friends, groups, recently played with, etc)

### Non Functionnal Requirements:

- High avaibility
- Scalable system
- Resilience
-

### Entities

1- Game:  represent a specific game. It can be Fortnite, League of Legend or any other game
2- Match: match for a specific game.
3- Score: consist of the 3 properties:

    {
	    userId: the id of the player
	    score: the number of eliminations.
	    rank: his global rank for this specific leaderboard.
    }

### API

We can a REST API since it's wedelly used and supported. A system like this one can have numorous endpoints: login, fetching user infos, games infos. But we will focus on the 3 main endpoints: creating a leaderboard, updating the score leaderboard and fetching a leaderboard.

**Create a a new leaderboard:** This endpoint is used to create a leaderboard. A leaderboard can be created for multiple purpose. For one specific game, for a group of friends, etc... Cl

    POST /v1/games/{gameId}/leaderboards
    
    Request body: {
    	type: "PRIVATE", "PUBLIC"
	    members: userId[] // optionnal field, only used if it's a private game between friends
    }
    
    Response body: {
		leaderboardId
	}
    
    Response code: 
    201 Created if leaderboard created
    400 BAD Request if request is malformed

**Update the score in a specific leaderboard:** Once a match is completed, we send the score to update the 	leaderboard.

    POST /v1/games/{gameId}/leaderboards/{leaderboardId}
    
    Request body: {
    	scores: Scores[]
    }
    
    Response body: {
		scores: Scores[]
	}
    
    Response code: 
    200 OK if score successfully updated
    400 BAD Request if request is malformed

**Fetching the leaderboards:** Users can query and see the different leaderboard. The filter are dore by query param and the response is paginated since we can have a long list of leaderboards.

    GET /v1/games/{gameid}/leaderboards?type=
    
    Response body: {
	    leaderboards: {
			leaderboard1: Scores[],
			leaderboard2: Scores[],
			leaderboard3: Scores[]
			...
		}
		size: total number of leaderboard
		nextURL: url of next set of leaderboards
	}

### Architecture

Here is a high level design of our architecture.

![image](https://github.com/arachid/clockwize-and-leaderboard/assets/29342184/2782da29-b3d9-4118-b632-a99611c47507)

We will use a distributed architecture to serve a scalable and fault-tolerant solution. We are going to use AWS as
our cloud hosting solution. It offers us multiple solutions out of the box and data centers worldwide to serve our users with low latency.

**Client:** The Client is the local machine of the users who play the game. Depending on the type of game, it can be a computer or a mobile device.

**Geo DNS:** Since low latency is crucial in a gaming environment, we will use Geo DNS to route the request to the nearest data center. AWS offers Geo DNS in a Route 53 configuration.

**Gateway:** We will use AWS ALB as a gateway solution. It offers us HTTPS support for secure communication and load balancing.

**Game Server:** The game server hosts the game match. Every player playing the same match is connected to the same server. A table maintains a map between the match and the server's URI to route the users to the correct server. The game server can scale on demand, and we can add or remove the server from our table. We will need bi-directional communication to achieve real-time. The Game server will keep the game score in-memory for fast read/write. While keeping the score in memory, we write through the disk using the [write-ahead log](https://en.wikipedia.org/wiki/Write-ahead_logging) technique to throw a write event on the disk. That way, the persisted logs can be replayed, and we can obtain the score even if the game server crashes. Once the match finishes, the game server posts the score to the Leaderboard Microservice. We can only trust our internal Game Server to update the score since we can't blindly trust the score coming from the client side. 

We use NodeJS for game service since it supports web sockets and real-time solutions. In addition, Nodejs servers use an Even-Loop architecture that lets them handle thousands of requests and connections per minute.

**Leaderboard Microservice:** This microservice operates CRUD on the leaderboard scores. On each Score update, it publishes it to a Kafka Queue. Using this asynchronous communication lets us write faster. The technologies used to write this microservice only matter a little for this service. Since Java has great frameworks for implementing microservices like Spring Cloud, we decided to go with this stack here.

**Score Queue:** This is used to collect the score. We are using Kafka because it's a popular and reliable solution for streaming messages. Using a queue, we can independently scale our producers (Leaderboard Service) and our consumers (Score Agregatore). Kafka offers scalability using its partition system and high availability with partition replications.

**Score Aggregator:** Subscribe to new scores, aggregate the result and pre-process the scores for the different time frames: weekly, monthly and all-time leaderboard. That way, the pre-processed result returns fast when the user's query leaderboards. Apache Spark is indeed well-suited for real-time stream processing, especially when used in combination with Apache Kafka. 

**LeaderBoard Redis Cluster:** Redis offers a great set of data management solutions. It's the most popular solution for in-memory caching and offers persistence and many other tools.
We will have a replication set for high availability that replicates the data from the primary nodes. If a primary node crashes, a replica can be elected to take its place and achieve high availability. 

We will be using [Redis Sorted Set](https://redis.io/docs/latest/develop/data-types/sorted-sets/) to achieve fast retrieval for top K scores, specific user scores and fast score updates. Redis utilizes an in-memory HashMap and a skip list
to achieve add, remove, or update scores in a speedy way (in a O(log n) when n is the number of elements)

Let's do some **rough estimation** to see if our leaderboard can fit in memory.

The leaderboard will consist of a list of eliminations/scores:

`[
	{ userdId: 1777737510428868608, score: 127312, rank: 1 },
 	{ userdId: 1723137510428863231, score: 127300, rank: 2 },
  	{ userdId: 1232737510428123234, score: 127200, rank: 3 }
   	...
]`

A score looks like this:
1-userId: snowflake id of size 8 bytes
2-score: an integer of 4 bytes
3-rank: an integer of 4 bytes

Total size per score: 16 byte

Since our requirement is 10 million concurrent users, we can assume that the total number of users is 50 times that, so 500 million users. By the way, 500 million users represent the number of registered users in Fortnite.

500 000 000 * 16bytes = 8 GB

To be conservative, since Redis Sorted Set uses a skip list and hashmap, we can double the size to 16 GB for the pointers, indexes and other overhead.

Modern hardware can easily handle the 16 gigs of memory load. 16 gigs are our worst-case scenario: load in memory all-time scores for all the registered users of a major game like Fortnite. Obviously, we can optimize that, we don't need to load un-active users. But, our rough estimation demonstrates that we can use an in-memory solution to handle hundreds of millions with low latency.


**Reconciliation: ** We can set up a daily cron job that replays the score updates from our Kafka queue and verifies that the score matches what we have in the database. That way, we can monitor our system's reliability.


### Follow Up

We can easily spend weeks designing the perfect leaderboard system. But, since it's a 4-5 hour exercise, we defined requirements, our API and a high-level architecture. There are a few things that we can dive deeper into if we had additional time:

-Using geo-sharding to scale our database and keep the data closer to the users geographically
-Deep dive into how Redis Sorted Set uses Skip tables to access the top K score in an O(K) time and makes score updates in an O(log n), making it very efficient.
-Define a partition strategy in our Kafka cluster to guarantee the order of message delivery to our Redis Cluster and consistent score
-Other features: Authentication and fetching user info (name, picture, country) while fetching its score.
