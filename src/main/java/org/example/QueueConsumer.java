package org.example;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableRabbit
@Slf4j
public class QueueConsumer {

    private final EventNotifier eventNotifier;

    public QueueConsumer(EventNotifier eventNotifier) {
        this.eventNotifier = eventNotifier;
    }

    @RabbitListener(queues = "myQueue")
    public void listen(String message) {
        log.info("Message received: " + message);
        eventNotifier.onEvent(message);
    }
}
