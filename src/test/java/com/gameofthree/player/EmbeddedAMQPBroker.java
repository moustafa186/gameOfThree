package com.gameofthree.player;

import com.google.common.io.Files;
import org.apache.qpid.server.Broker;
import org.apache.qpid.server.BrokerOptions;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.SocketUtils;

@Component
public class EmbeddedAMQPBroker {

    public static final int BROKER_PORT = SocketUtils.findAvailableTcpPort();
    public static final String CONFIG_FILE_PATH = "qpid-config.json";
    public static final String PASSWORD_FILE_PATH = "passwd.properties";

    private final Broker broker = new Broker();

    public void EmbeddedAMQPBroker() throws Exception {
        // prepare options
        final BrokerOptions brokerOptions = new BrokerOptions();
        brokerOptions.setConfigProperty("qpid.amqp_port", String.valueOf(BROKER_PORT));
        brokerOptions.setConfigProperty("qpid.pass_file", PASSWORD_FILE_PATH);
        brokerOptions.setConfigProperty("qpid.work_dir", Files.createTempDir().getAbsolutePath());
        brokerOptions.setInitialConfigurationLocation(CONFIG_FILE_PATH);
        // start broker
        broker.startup(brokerOptions);
    }

    public void createExchange(String exchangeName, String queueName, String routingKey) {
        final CachingConnectionFactory cf = new CachingConnectionFactory(BROKER_PORT);
        final RabbitAdmin admin = new RabbitAdmin(cf);
        final Queue queue = new Queue(queueName, false);
        admin.declareQueue(queue);
        final TopicExchange exchange = new TopicExchange(exchangeName);
        admin.declareExchange(exchange);
        admin.declareBinding(BindingBuilder.bind(queue).to(exchange).with(routingKey));
        cf.destroy();
    }

    public void deleteExchange(String exchangeName) {
        final CachingConnectionFactory cf = new CachingConnectionFactory(BROKER_PORT);
        final RabbitAdmin admin = new RabbitAdmin(cf);
        admin.deleteExchange(exchangeName);
        cf.destroy();
    }

    public void sendMessage(String exchangeName, String routingKey, String message) {
        final CachingConnectionFactory cf = new CachingConnectionFactory(BROKER_PORT);
        final RabbitTemplate template = new RabbitTemplate(cf);
        template.convertAndSend(exchangeName, routingKey, message);
        cf.destroy();
    }
}
