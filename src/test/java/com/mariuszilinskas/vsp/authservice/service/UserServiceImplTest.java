package com.mariuszilinskas.vsp.authservice.service;

import com.mariuszilinskas.vsp.authservice.client.UserFeignClient;
import com.mariuszilinskas.vsp.authservice.dto.AuthDetails;
import com.mariuszilinskas.vsp.authservice.enums.UserRole;
import com.mariuszilinskas.vsp.authservice.enums.UserStatus;
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
    private final String email = "user@email.com";
    private AuthDetails authDetails;
    private final FeignException feignException = TestUtils.createFeignException();

    // ------------------------------------

    @BeforeEach
    void setUp() {
        authDetails = new AuthDetails(userId, List.of(UserRole.USER), List.of(), UserStatus.ACTIVE);
    }

    // ------------------------------------

    @Test
    void testGetUserAuthDetailsWithEmail_Success() {
        // Arrange
        when(userFeignClient.getUserAuthDetailsByEmail(email)).thenReturn(authDetails);

        // Act
        userDetailsService.getUserAuthDetailsWithEmail(email);

        // Assert
        verify(userFeignClient, times(1)).getUserAuthDetailsByEmail(email);
    }

    @Test
    void testGetUserAuthDetailsWithEmail_FeignException() {
        // Arrange
        doThrow(feignException).when(userFeignClient).getUserAuthDetailsByEmail(email);

        // Act & Assert
        assertThrows(FeignClientException.class, () ->  userDetailsService.getUserAuthDetailsWithEmail(email));

        // Assert
        verify(userFeignClient, times(1)).getUserAuthDetailsByEmail(email);
    }

    // ------------------------------------

    @Test
    void testGetUserAuthDetailsWithId_Success() {
        // Arrange
        when(userFeignClient.getUserAuthDetailsByUserId(userId)).thenReturn(authDetails);

        // Act
        userDetailsService.getUserAuthDetailsWithId(userId);

        // Assert
        verify(userFeignClient, times(1)).getUserAuthDetailsByUserId(userId);
    }

    @Test
    void testGetUserAuthDetailsWithId_FeignException() {
        // Arrange
        doThrow(feignException).when(userFeignClient).getUserAuthDetailsByUserId(userId);

        // Act & Assert
        assertThrows(FeignClientException.class, () ->  userDetailsService.getUserAuthDetailsWithId(userId));

        // Assert
        verify(userFeignClient, times(1)).getUserAuthDetailsByUserId(userId);
    }

    // ------------------------------------

}
