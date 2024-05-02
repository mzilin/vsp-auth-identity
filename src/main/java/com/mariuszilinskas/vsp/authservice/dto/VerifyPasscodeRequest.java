package com.mariuszilinskas.vsp.authservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record VerifyPasscodeRequest(
        @NotBlank(message = "pinCode cannot be blank")
        @Size(min = 6, max = 6, message = "passcode must be 6 characters")
        String passcode
){}
