package com.gameofthree.player;

import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class GameSender {
    @Autowired
    private Environment env;

    private String playerName;

    @Autowired
    private RabbitTemplate template;

    @Autowired @Qualifier(value = "gameTurnsTopic")
    private TopicExchange turnsTopic;

    @Autowired @Qualifier(value = "gameWinTopic")
    private TopicExchange winTopic;

    public void publishTurn(int value) {
        template.convertAndSend(turnsTopic.getName(), env.getProperty("player.name") + ".turns", value );
    }

    public void publishWin() {
        playerName = env.getProperty("player.name");
        template.convertAndSend(winTopic.getName(), playerName + ".win", playerName + " wins");
    }
}
