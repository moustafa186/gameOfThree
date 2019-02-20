package com.gameofthree.player;

import com.gameofthree.player.engine.AutoPlay;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;

public class GameReceiver {

    @Autowired
    private AutoPlay autoPlay;

    @RabbitListener(queues = "#{turnsQueue.name}")
    public void receiveTurn(int value) {
        autoPlay.play(value);
    }

    @RabbitListener(queues = "#{winQueue.name}")
    public void receiveWin() {
        System.out.println("You LOSE!");
    }

}
