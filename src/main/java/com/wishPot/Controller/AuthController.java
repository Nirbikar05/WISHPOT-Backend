package com.wishPot.Controller;

import com.wishPot.Dto.AuthenticationResponse;
import com.wishPot.Dto.LoginRequest;
import com.wishPot.Dto.RegistrationRequest;
import com.wishPot.Dto.RoleResponse;
import com.wishPot.Service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegistrationRequest request) {
        authService.registerUser(request);
        return ResponseEntity.ok("User registered successfully!");
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(@RequestBody LoginRequest request) {
        AuthenticationResponse response = authService.authenticateUser(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/role/{username}")
    public ResponseEntity<RoleResponse> getRole(@PathVariable String username) {
        RoleResponse response = authService.getUserRole(username);
        return ResponseEntity.ok(response);
    }
}
