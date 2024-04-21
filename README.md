# Clockwise Matrix Algo & Leaderboard System Design Assesment

## Question 1: Clockwise Matrix Algo

We Wrote a function that, given a matrix of integers, builds a string with the entries of that matrix
appended in clockwise order.

    String arrayClockWise(int[][] matrix) {
    ...
    }

For instance, the 3x4 matrix below:

	2, 3, 4, 8
	5, 7, 9, 12
	1, 0, 6, 10

We would make a string like this “2, 3, 4, 8, 12, 10, 6, 0, 1, 5, 7, 9”.

This problem can be solved iteratively and recursively. We used the recursive approach to make the code concise and easy to read. We traverse to the array, and when we hit a size boundary or a visited cell, we change direction following clockwise directions. When we can't move anymore, the recursion ends.

**Time Complexity:** O(n) since we are traversing each element once.

**Memory Complexity:** O(n) since we have to create a visited matrix.

**Test coverage:** 100% with multiple scenarios: square, rectangle, one line, one column & empty matrix.

**Limitation:** For this exercise, we decided to support only rectangle and square matrices. However, we can extend this code to support other shapes (e.g. triangles shapes) or add validation for unsupported shapes.

## Question 2: Leaderboard System Design

Leaderboards are in almost any multiplayer video game. We have a task to design a leaderboard system for a video game company with over 10 million concurrent players.

### Fonctionnal Requirements
- Leaderboards are based on the total number of eliminations across all games. Each elimination counts for a point and eacrease the score.
- There are four supported games and each leaderboard supports a game
- System should Have daily / weekly / all-time aggregations
- Have user-driven filtering capabilities (friends, groups, recently played with, etc)

### Non-Functional Requirements:

- High availability
- Scalable system
- Resilience
- Low latency

### Entities

- **Game:**  represents a specific game. It can be Fortnite, League of Legends or any other game.
- **Match:** match for a specific game.
- **Leaderboard:** a set of scores related to the same purpose: same game, same match, same friend group or same timeframe...
- **Score:** consist of the three properties:

```
{
    userId: the id of the player.
    score: the number of eliminations and other points.
    rank: his global rank for this specific leaderboard.
}
```
### API

We can use a REST API since it's widely used and supported. A system like this one can have many endpoints: login, fetching user information, and fetching game information. But we will focus on the four primary endpoints: creating a leaderboard, updating the score of a leaderboard, fetching leaderboards and fetching scores. All the endpoints require authentification, and our backend will verify that a specific user is authorized to access this leaderboard.

**Create a new leaderboard:** This endpoint is used to create a leaderboard. A leaderboard can be created for multiple purposes, such as a global all-time game, a weekly leaderboard, a private group of friends, etc. We will restrict access to creating an all-time leaderboard and other big leaderboards only internally since they consume a lot of resources.

    POST /v1/games/{gameId}/leaderboards
    
    Request body: {
    	    type: "PRIVATE" | "PUBLIC" | "TOURNAMENT"...
	    members: userId[] // optional field, only used if it's a private group between friends
    }
    
    Response body: {
		leaderboardId
	}
    
    Response code: 
    201 Created if leaderboard created
    400 BAD Request if the request is malformed

**Update the score in a specific leaderboard:** Once a match is completed, we send the score to update a specific leaderboard by specifying the leaderboard.

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

**Fetching the leaderboards:** Users can query and see the different leaderboards. Filters are specified in the query param. The response is paginated since we can have a long list of leaderboards.

    GET /v1/games/{gameid}/leaderboards?timeframe={recent | weekly | all-time}&group={groupId}&page={page}&size={paginationSize}
    
    Response body: {
	    leaderboards: {
			leaderboardId1: Scores[],
			leaderboardId2: Scores[],
			leaderboardId3: Scores[]
			...
		}
		size: total number of leaderboard
		nextURL: url of next set of leaderboards
	}
 
**Fetching the score for a specific leaderboard:** Users can see the score for a specific Leaderboard and specify a set of user IDs they want to see.

     GET /v1/games/{gameid}/leaderboards/{leaderboardId}/scores&playerIds={userId[]}&page={page}&size={paginationSize}
    
    Response body: {
	    scores: scores[]
	    size: total number of leaderboard
	    nextURL: url of next set
	}

### Architecture

Here is a high-level design of our architecture.

![image](https://github.com/arachid/clockwize-and-leaderboard/assets/29342184/e0bb9a1f-ad99-441e-82c8-80a426b48526)

We will use a distributed architecture to serve a scalable and fault-tolerant solution. We are going to use AWS as
our cloud provider solution. It offers us multiple solutions out of the box and data centers worldwide to serve our users with low latency.

**Client:** The Client is the local machine of the users who play the game. Depending on the type of game, it can be a computer or a mobile device.

**Geo DNS:** Since low latency is crucial in a gaming environment, we will use Geo DNS to route the request to the nearest data center. AWS offers Geo DNS in a Route 53 configuration.

**Gateway:** We will use AWS ALB as a gateway solution. It offers us HTTPS support for secure communication and load balancing.

**Game Server:** The game server hosts the game match. Every player playing the same match is connected to the same server. A table maintains a map between the match and the server's URI to route the users to the correct server. The game server can scale on demand, and we can add or remove the server from our table. We will need bi-directional communication to achieve real-time. The Game server will keep the game score in-memory for fast read/write. While keeping the score in memory, we write through the disk using the [write-ahead log](https://en.wikipedia.org/wiki/Write-ahead_logging) technique to persist the events on the disk. That way, the persisted logs can be replayed, and we can obtain the score even if the game server crashes. Once the match finishes, the game server posts the score to the Leaderboard Microservice. We can only trust our internal Game Server to update the score since we can't blindly trust the data coming from the client side. 

We use NodeJS for game service since it supports web sockets and real-time solutions. In addition, Nodejs servers use an Even-Loop architecture that lets us handle thousands of requests and connections simultaneously.

**Leaderboard Microservice:** This microservice operates CRUD on the leaderboard and scores. Each Score update from our Game Server is published to a Kafka Queue. Using this asynchronous communication lets us write faster. The technologies used to write this microservice only matter a little for this service. Since Java has great frameworks for implementing microservices like Spring Cloud, we decided to go with this stack here. Since it's a stateless service, we can easily horizontally scale it using auto-scaling.

**Score Queue:** This is used to collect the score. We are using Kafka because it's a popular and reliable solution for streaming messages. Whit this queue, we can independently scale our producers (Leaderboard Microservice) and our consumers (Score Agregatore). Kafka offers scalability using its partition system and is highly available with partition replications.

**Score Aggregator:** Subscribe to new scores, aggregate the result and pre-process the scores for the different time frames: recent, weekly, monthly and all-time leaderboard. That way, the pre-processed result returns fast when the users query scores. Apache Spark is indeed well-suited for real-time stream processing, especially when used in combination with Apache Kafka. 

**LeaderBoard Redis Cluster:** Redis offers a great set of data management solutions. It's the most popular solution for in-memory caching, offers persistence and many other tools.
We will have a replication set for high availability that replicates the data from the primary nodes. If a primary node crashes, a replica can be elected to take its place and achieve high availability. 
To scale horizontally our cluster, we can use sharding.

We will be using [Redis Sorted Set](https://redis.io/docs/latest/develop/data-types/sorted-sets/) to achieve fast retrieval for top K scores, specific scores and fast score updates. Redis utilizes an in-memory HashMap and a skip list
to achieve add, remove, or update scores in a speedy way (in approximately a O(log n) where n is the number of scores entries)

Let's do some **rough estimation** to see if our leaderboard can fit in memory.

The leaderboard will consist of a list of eliminations/scores:
```
[
	{ userdId: 1777737510428868608, score: 127312, rank: 1 },
 	{ userdId: 1723137510428863231, score: 127300, rank: 2 },
  	{ userdId: 1232737510428123234, score: 127200, rank: 3 }
   	...
]
```

A score looks like this:
1-userId: snowflake id of size 8 bytes
2-score: an integer of 4 bytes
3-rank: an integer of 4 bytes

Total size per score: 16 byte

Since our requirement is 10 million concurrent users, we can assume that the total number of users is 50 times that, so 500 million users. By the way, 500 million users represent the number of registered users in Fortnite.

500 000 000 * 16bytes = 8 GB

To be conservative, since Redis Sorted Set uses a skip list and hashmap, we can double the size to 16 GB for the pointers, indexes and other overhead.

Modern hardware can easily handle 16 GB of memory load. 16 GB is our worst-case scenario: load in memory all-time scores for all the registered users of a major game like Fortnite. We can optimize that; we don't need to load inactive users. However, our rough estimation demonstrates that we can use an in-memory solution to handle hundreds of millions of entries for a major game like Fortnite with low latency.

**Reconciliation: ** We can set up a daily cron job that replays some score updates from our Kafka queue and verifies that the score matches what we have in the database. That way, we can monitor our system consistency and detect bugs.

### Follow Up

We can easily spend weeks designing the perfect leaderboard system. However, since it's a 4-5 hour exercise, we defined the requirements, our API, and a high-level architecture. There are a few things that deserve to dive deeper into if we have additional time:

- Using geo-sharding to scale our database and keep the data closer to the users geographically
- Deep dive into how Redis Sorted Set uses Skip tables to access the top K score in an O(K) time and makes score updates in an O(log n), making it very efficient.
- Define a partitioning strategy in our Kafka cluster to guarantee the order of message delivery to our Redis Cluster and a consistent score.
- Other features: authentication and fetching user info (name, picture, country) while fetching user scores.
