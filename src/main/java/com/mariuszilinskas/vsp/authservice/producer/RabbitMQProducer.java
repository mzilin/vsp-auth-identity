package com.mariuszilinskas.vsp.authservice.producer;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RabbitMQProducer {

    private static final Logger logger = LoggerFactory.getLogger(RabbitMQProducer.class);
    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.exchange}")
    private String exchange;

    @Value("${rabbitmq.routing-keys.platform-emails}")
    private String platformEmailsRoutingKey;

//    public void sendVerificationEmailMessage(Object request) {
//        logger.info("Sending message to create default user profile: {}", request);
//        rabbitTemplate.convertAndSend(exchange, platformEmailsRoutingKey, request);
//    }

}
