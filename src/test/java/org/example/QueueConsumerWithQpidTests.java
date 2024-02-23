package org.example;

import lombok.SneakyThrows;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.Lifecycle;
import org.springframework.test.context.ActiveProfiles;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.mockito.Mockito.verify;

@SpringBootTest(classes = QueueConsumer.class)
@ImportAutoConfiguration(RabbitAutoConfiguration.class)
@ActiveProfiles("test")
public class QueueConsumerWithQpidTests {

    public static final String QUEUE_NAME = "myQueue";
    public static final String EXCHANGE_NAME = "myExchange";
    public static final String ROUTING_KEY = "#";
    private static QpidBroker qpidBroker;

    @Autowired
    private RabbitTemplate template;

    @Autowired
    private RabbitAdmin admin;

    @Autowired
    private RabbitListenerEndpointRegistry rabbitListenerEndpointRegistry;

    @MockBean
    EventNotifier eventNotifier;

    @BeforeAll
    public static void beforeAll() {
        qpidBroker = new QpidBroker();
        qpidBroker.startup();
    }

    @AfterAll
    public static void afterAll() {
        qpidBroker.shutdown();
    }

    @BeforeEach
    public void beforeEach() {
        declareTopology();

        rabbitListenerEndpointRegistry.getListenerContainers().forEach(Lifecycle::start);
    }

    @Test
    @SneakyThrows
    public void shouldInvokeEventNotifier() {
        var message = RandomStringUtils.randomAlphanumeric(10);

        template.convertAndSend(EXCHANGE_NAME, ROUTING_KEY, message);

        await().atMost(5, TimeUnit.SECONDS).until(() -> hasInvokedEventNotifier(message));
    }

    private void declareTopology() {
        var queue = new Queue(QUEUE_NAME, false);
        admin.declareQueue(queue);
        var exchange = new DirectExchange(EXCHANGE_NAME);
        admin.declareExchange(exchange);
        admin.declareBinding(BindingBuilder.bind(queue).to(exchange).with(ROUTING_KEY));
    }

    private Boolean hasInvokedEventNotifier(String expectedMessage) {
        try {
            verify(eventNotifier).onEvent(expectedMessage);
            return true;
        } catch(Throwable e) {
            return false;
        }
    }
}

