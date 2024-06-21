package com.mariuszilinskas.vsp.authservice.consumer;

import com.mariuszilinskas.vsp.authservice.dto.ResetPasscodeRequest;
import com.mariuszilinskas.vsp.authservice.service.DataDeletionService;
import com.mariuszilinskas.vsp.authservice.service.PasscodeService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RabbitMQConsumer {

    private static final Logger logger = LoggerFactory.getLogger(RabbitMQConsumer.class);
    private final PasscodeService passcodeService;
    private final DataDeletionService dataDeletionService;

    @RabbitListener(queues = "${rabbitmq.queues.create-passcode}")
    public void consumeCreatePasscodeMessage(ResetPasscodeRequest request) {
        logger.info("Received request to create user passcode: {}", request);
        passcodeService.resetPasscode(request);
    }

    @RabbitListener(queues = "${rabbitmq.queues.delete-user-data}")
    public void consumeDeleteUserDataMessage(UUID userId) {
        logger.info("Received request to delete user data for User [userId: {}]", userId);
        dataDeletionService.deleteUserAuthData(userId);
    }

}
