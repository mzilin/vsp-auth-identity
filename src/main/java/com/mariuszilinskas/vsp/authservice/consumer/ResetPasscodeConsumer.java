package com.mariuszilinskas.vsp.authservice.consumer;

import com.mariuszilinskas.vsp.authservice.dto.ResetPasscodeRequest;
import com.mariuszilinskas.vsp.authservice.service.PasscodeService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ResetPasscodeConsumer {

    private static final Logger logger = LoggerFactory.getLogger(ResetPasscodeConsumer.class);
    private final PasscodeService passcodeService;

    @RabbitListener(queues = "${rabbitmq.queues.create-passcode}")
    public void consumeResetPasscodeMessage(ResetPasscodeRequest request) {
        logger.info("Received request to create user passcode: {}", request);
        passcodeService.resetPasscode(request);
    }

}
