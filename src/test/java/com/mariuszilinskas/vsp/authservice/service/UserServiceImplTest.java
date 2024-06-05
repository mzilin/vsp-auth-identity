package com.mariuszilinskas.vsp.authservice.service;

import com.mariuszilinskas.vsp.authservice.client.UserFeignClient;
import com.mariuszilinskas.vsp.authservice.dto.AuthDetails;
import com.mariuszilinskas.vsp.authservice.exception.FeignClientException;
import com.mariuszilinskas.vsp.authservice.util.TestUtils;
import feign.FeignException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    private UserFeignClient userFeignClient;

    @InjectMocks
    UserServiceImpl userDetailsService;

    private final UUID userId = UUID.randomUUID();
    private final FeignException feignException = TestUtils.createFeignException();

    // ------------------------------------

    @BeforeEach
    void setUp() {}

    // ------------------------------------

    @Test
    void testGetUserAuthDetails_Success() {
        // Arrange
        String email = "some@email.com";
        AuthDetails authDetails = new AuthDetails(userId, List.of("USER"), List.of());
        when(userFeignClient.getUserAuthDetails(email)).thenReturn(authDetails);

        // Act
        userDetailsService.getUserAuthDetails(email);

        // Assert
        verify(userFeignClient, times(1)).getUserAuthDetails(email);
    }

    @Test
    void testGetUserAuthDetails_FeignException() {
        // Arrange
        String email = "some@email.com";
        doThrow(feignException).when(userFeignClient).getUserAuthDetails(email);

        // Act & Assert
        assertThrows(FeignClientException.class, () ->  userDetailsService.getUserAuthDetails(email));

        // Assert
        verify(userFeignClient, times(1)).getUserAuthDetails(email);
    }

    // ------------------------------------

}
