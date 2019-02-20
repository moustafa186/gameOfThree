package com.gameofthree.player.api;

import com.gameofthree.player.GameSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class ManualPlayController {
    @Autowired
    private Environment env;

    @Autowired
    private GameSender sender;

    private String playerName, opponentName;

    public ManualPlayController(GameSender sender) {
        this.sender = sender;
    }

    @PostMapping("/start")
    @ResponseStatus(HttpStatus.OK)
    public void start(@RequestParam int startingValue){
        playerName = env.getProperty("player.name");
        opponentName = env.getProperty("opponent.name");

        System.out.println(playerName + ": Starting game manually!");
        System.out.println(playerName + ": Sending "+ startingValue + " to " + opponentName);
        sender.publishTurn(startingValue);
    }

    @PostMapping("/play")
    public ResponseEntity<Object> play(@RequestParam int value, @RequestParam int choice){
        playerName = env.getProperty("player.name");
        opponentName = env.getProperty("opponent.name");

        System.out.println(playerName + ": received number: " + value);
        System.out.println(playerName + ": added number: " + choice);

        if(choice < -1 || choice > 1)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Choice must be one of: -1, 0, 1");

        if((value + choice) % 3 == 0){
            int result = (value + choice) /3;
            System.out.println(playerName + ": Resulting number: " + result);
            System.out.println(playerName + ": Sending "+ result + " to " + opponentName);
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
            return ResponseEntity.status(HttpStatus.OK).body("choice is: " + choice + ", and result is: " + result );
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Sum of value and choice should be divisible by 3");
        }
    }
}
