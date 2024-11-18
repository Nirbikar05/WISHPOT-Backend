package com.wishPot.Dto;

import lombok.Data;

@Data
public class RegistrationRequest {
    private String username;
    private String password;
    private String email;
    private String role; // USER or ADMIN
}
