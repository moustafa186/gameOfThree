package com.gameofthree.player.config;

import com.gameofthree.player.GameReceiver;
import com.gameofthree.player.GameSender;
import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class RabbitMQConfig {

    private String playerName;

    @Bean @Qualifier(value = "gameTurnsTopic")
    public TopicExchange gameTurnsTopic() {
        return new TopicExchange("game.turns");
    }

    @Bean @Qualifier(value = "gameWinTopic")
    public TopicExchange gameWinTopic() {
        return new TopicExchange("game.win");
    }

    @Bean
    public GameSender sender() {
        return new GameSender();
    }

    private static class ReceiverConfig {

        @Autowired
        private Environment env;

        @Bean
        public GameReceiver receiver() {
            return new GameReceiver();
        }

        @Bean
        public Queue turnsQueue() {
            return new AnonymousQueue();
        }

        @Bean
        public Queue winQueue() {
            return new AnonymousQueue();
        }

        @Bean
        @Qualifier(value = "gameTurnsTopic")
        public Binding turnsBinding(@Qualifier(value = "gameTurnsTopic") TopicExchange topic,
                                 Queue turnsQueue) {
            return BindingBuilder.bind(turnsQueue)
                    .to(topic)
                    .with(env.getProperty("opponent.name") + ".turns");
        }

        @Bean
        public Binding winBinding(@Qualifier(value = "gameWinTopic") TopicExchange topic,
                                 Queue winQueue) {
            return BindingBuilder.bind(winQueue)
                    .to(topic)
                    .with(env.getProperty("opponent.name") + ".win");
        }
    }
}
