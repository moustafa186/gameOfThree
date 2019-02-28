# gameOfThree
Event-based implementation of game of three

# tech stack
Java 8, Spring (web, amqp), Spring boot, rabbitmq

# Architecture
- we have **2 Topic Exchanges**, one for players' turns and the other for win event.
- the player app acts as consumer and producer.
- each player **produces/sends** messages to topics with routing keys corresponding to **his name**, 
e.g. player 1 uses p1.turns and p1.win as routing keys for turns and win topics respectively.
- each player **consumes/receives messages** from the topic with routing keys corresponding to **his opponent's name**, 
e.g. player 1 uses p2.turns and p2.win as routing keys for turns and win topics respectively.
- the message/event sent to turns topic contains the result value after adding a number and dividing by 3.
- the message/event sent to win topic contains the winning player name e.g. p1.
- the above design choice makes the system scalable; where any number of players can play together pairwise.
- HTTP endpoints (**/start** and **/play**) are exposed to support manaual input from player

# how to run
- at the root directory of the project, run 
```
# start rabbitmq instance in docker container
docker run -d --hostname my-rabbit --name some-rabbit rabbitmq:3

# build the jar
./gradlew bootJar

# start player 2 in manual mode
java -jar ./build/libs/player-0.0.1-SNAPSHOT.jar --player.name=p2 --opponent.name=p1 --game.mode=manual --server.port=8081

# start player 1 in auto mode
java -jar ./build/libs/player-0.0.1-SNAPSHOT.jar --player.name=p1 --opponent.name=p2 --game.mode=auto --server.port=8080
```

# how to play
- If player instance is started in auto mode, then it randomly picks an integer and sends it to opponent (as an event). 
Also, on receiving a message that the opponent have played, it calculates the next number to be added, plays automatically, 
then sends the result as message to opponent.
- If player instance is started in manual mode, then use the endpoints to provide input as follows:

```
# to start:
curl -i -X POST \
   -d \
'' \
 'http://localhost:8080/start?startingValue=56'
```
```
# to play:
curl -i -X POST \
   -d \
'' \
 'http://localhost:8080/play?value=18&choice=0'
```
- Output will be logged to console


# Testing
- Embedded in-memory broker is used to mock rabbitMQ
- Test scenarios (Work In Progress)

## I. Auto Mode

- when auto player receives start message
-> it should play first turn

- when auto player receives turn message (with result not winning)
-> it should make the right choice
-> publish turn message to opponent

- when auto player receives turn message (with result winning)
-> it should make the right choice
-> it should show "You WIN!" message
-> it should publish win message

## II. Manual Mode

- when human player sends a start request
-> a turn message should be sent to opponent

- when human player sends a play request with valid choice and result is not win
-> a turn message should be sent to opponent

- when human player sends a play request with valid choice and result is a win
-> it should show "You WIN!" message
-> a win message should be sent to opponent

- when human player sends a play request with invalid choice
-> response status should be 400: BAD_REQUEST

## III. Both Modes

- when player receives win message
-> it should show "You LOSE!" message

# Future work
- handle offline player at the game start:
    - implement exponential back-off retry strategy
    
    or
    - add dead-letter queues to handle if one players is not available at the beginning of the game or if some connectivity failure happened during the game.
- dockerize app (multi-stage Dockerfile) and provide docker-compose file for easy startup.
