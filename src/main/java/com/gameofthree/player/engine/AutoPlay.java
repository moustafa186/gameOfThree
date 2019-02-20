package com.gameofthree.player.engine;

import com.gameofthree.player.GameSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class AutoPlay implements ApplicationRunner {
    @Autowired
    private Environment env;

    @Autowired
    private GameSender sender;

    private String playerName, opponentName;

    public AutoPlay(GameSender sender) {
        this.sender = sender;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        autoStart(env.getProperty("game.mode"));
    }

    public void autoStart(String gameMode) {
        if(gameMode.equals("auto")){
            playerName = env.getProperty("player.name");
            opponentName = env.getProperty("opponent.name");

            System.out.println(playerName + ": Starting game automatically!");
            Random r = new Random();
            int max = 100, min = 1;
            int randomStartingValue = r.nextInt((max - min) + 1) + min;
            System.out.println(playerName + ": Sending "+ randomStartingValue + " to " + opponentName);
            sender.publishTurn(randomStartingValue);
        }
    }

    public void play(int value){

        this.playerName = env.getProperty("player.name");
        this.opponentName = env.getProperty("opponent.name");

        System.out.println(playerName + ": received number: " + value);
        if(env.getProperty("game.mode").equals("auto")){

            // choose action: -1, 0, +1
            int mod = value % 3;
            int addedNumber = (mod == 0) ? 0 : (mod == 1) ? -1 : 1 ; // can be manual input
            System.out.println(playerName + ": Added number: " + addedNumber);

            int result = (value + addedNumber) / 3;
            System.out.println(playerName + ": Resulting number: " + result);

            // if win
            if(result == 1) {
                // publish win event
                System.out.println("You WIN!");
                sender.publishWin();
            } else {
                // publish play event with result and other player id
                System.out.println(playerName + ": Sending "+ result + " to " + opponentName);
                sender.publishTurn(result);
            }
        } else {
            System.out.println("Please choose from {-1, 0, 1} and send request as:");
            System.out.println("http://localhost:"+ env.getProperty("server.port") + "/play?value=" + value + "&choice=[-1|0|1]");
        }
    }
}
