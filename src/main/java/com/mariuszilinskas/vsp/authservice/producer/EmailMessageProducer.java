package com.mariuszilinskas.vsp.authservice.producer;

import com.mariuszilinskas.vsp.authservice.dto.VerifyEmailRequest;
import com.mariuszilinskas.vsp.authservice.dto.WelcomeEmailRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailMessageProducer {

    private static final Logger logger = LoggerFactory.getLogger(EmailMessageProducer.class);
    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.exchange}")
    private String exchange;

    @Value("${rabbitmq.routing-keys.platform-emails}")
    private String routingKey;

    public void sendVerificationEmailMessage(VerifyEmailRequest request) {
        logger.info("Sending Verification Email message: {}", request);
        rabbitTemplate.convertAndSend(exchange, routingKey, request);
    }

    public void sendWelcomeEmailMessage(WelcomeEmailRequest request) {
        logger.info("Sending Welcome Email message: {}", request);
        rabbitTemplate.convertAndSend(exchange, routingKey, request);
    }

}
