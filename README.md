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

We are going to use a distributed architecture to be able to serve a scalable and fault-tolerant solution. We are going to use AWS as
our cloud hosting solution. It offers us a couple of solutions out of the box and multiple data centers across the world to serve our users with low latency.

**Client:** The Client is the local machine of the users who play the game. Depending on the type of game, it can be a computer or a mobile device.

**Geo DNS:** Since low latency is crucial in a gaming environment, we will use Geo DNS to route the request to the nearest data center. AWS offers Geo DNS in a Route 53 configuration.

**Gateway:** We will use AWS ALB as a gateway solution. It offers us HTTPS support for secure communication and load balancing.

**Game Server:** The game server hosts the game match. Every player playing the same match is connected to the same server. A table maintains a map between the match and the server's URI to route the users to the correct server. The game server can scale on demand, and we can add or remove the server from our table. We will need bi-directional communication to achieve real-time. The Game server will keep the game score in memory for fast read and write. As mentioned, in gaming, the low-latency is crucial. While keeping the score in memory, we write through the disk using the [write-ahead log](https://en.wikipedia.org/wiki/Write-ahead_logging) technique to throw the disk. That way, we can persist the score on the disk in case of a crash. Once the match finish, the game server post the score to the Leaderboard Microservice. We can only trust our internal Game Server to update the score since we can't blindly trust the score coming from the client side. 

We use NodeJS for game service since it supports web sockets and real-time solutions. In addition, Nodejs servers use an Even-Loop architecture that lets them handle thousands of requests and connections per minute.

**Leaderboard Microservice:** This microservice operates CRUD on the leaderboard scores. On each Score update, it publishes it to a Kafka Queue. Using this asynchronous communication lets us have a faster write.

**Score Queue: **


### Follow Up

We can easily spend days designing the perfect leaderboard. But, since it's 4-5 hours of exercise, we defined requirements, our API and a high-level architecture. There are a few things that we could dive deep into if we had additional time:

-Using geo-sharding to scale our database and keep the data closer to the user geographically
-Deep dive into how Redis Sorted Set uses Skip tables to give access to the top K score in a case-constant time.
-Partionne will use our Kafka cluster to guarantee the order and the deliveries of the message to our Redis Cluster.


