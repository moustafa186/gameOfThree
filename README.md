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

# Future work
- add dead-letter queues to handle if one players is not available at the begining of the game or if some connectivity failure happened during the game.
- dockerize app (multi-stage Dockerfile) and provide docker-compose file for easy startup.
