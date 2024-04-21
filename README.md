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

We will use public cloud since it's


![image](https://github.com/arachid/clockwize-and-leaderboard/assets/29342184/2782da29-b3d9-4118-b632-a99611c47507)


### Limitations

Since, it's a 4-5 hours excice
