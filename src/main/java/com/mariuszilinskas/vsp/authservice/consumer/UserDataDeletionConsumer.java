package com.mariuszilinskas.vsp.authservice.consumer;

import com.mariuszilinskas.vsp.authservice.service.DataDeletionService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserDataDeletionConsumer {

    private static final Logger logger = LoggerFactory.getLogger(UserDataDeletionConsumer.class);
    private final DataDeletionService dataDeletionService;

    @RabbitListener(queues = "${rabbitmq.queues.delete-user-data}")
    public void consumeDeleteUserDataMessage(UUID userId) {
        logger.info("Received request to delete user data for User [userId: {}]", userId);
        dataDeletionService.deleteUserAuthData(userId);
    }

}
