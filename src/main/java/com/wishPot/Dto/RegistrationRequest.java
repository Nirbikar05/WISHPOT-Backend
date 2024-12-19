package com.wishPot.Dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationRequest {
    private String username;
    private String password;
    private String email;
    private String role = "USER"; // Default role is USER

    public RegistrationRequest(String username,String password,String email){
        this.username = username;
        this.password = password;
        this.email = email;
    }
}
