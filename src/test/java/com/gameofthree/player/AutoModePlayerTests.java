package com.gameofthree.player;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AutoModePlayerTests {
	static {
		System.setProperty("game.mode", "auto");
		System.setProperty("player.name", "p1");
		System.setProperty("opponent.name", "p2");
	}
	@Autowired
	EmbeddedAMQPBroker broker;

	@BeforeClass
	public void initializeTopicExchangesAndQueues() {
		broker.createExchange("gameTurnsTopic", "p1.turns", "p2.turns");
		broker.createExchange("gameWinTopic", "p1.win", "p2.win");
	}

	@Test
	public void startMessageReceived_shouldPlayFirstTurn() {
		broker.sendMessage("gameTurnsTopic", "p1.turns", "15");
		// assert it played first turn: message sent to opponent
	}


	@Test
	public void turnMessageReceived_nonWinningTurn_shouldPlayTurn() {
		broker.sendMessage("gameTurnsTopic", "p1.turns", "16");
		// assert it played turn: message sent to opponent
	}

	@Test
	public void turnMessageReceived_winningTurn_shouldPlayTurnAndAnnounceWin() {
		broker.sendMessage("gameTurnsTopic", "p1.turns", "3");
		// assert it played turn: message sent to opponent
		// assert it wins: win message is published
	}

	@Test
	public void winMessageReceived_shouldEndGameWithLose() {
		broker.sendMessage("gameTurnsTopic", "p1.turns", "3");
		// assert it played turn: message sent to opponent
		// assert it wins: win message is published
	}
}
