package com.wishPot.Service;

import com.wishPot.Dto.AuthenticationResponse;
import com.wishPot.Dto.LoginRequest;
import com.wishPot.Dto.RegistrationRequest;
import com.wishPot.Dto.RoleResponse;
import com.wishPot.model.User;
import com.wishPot.Repository.UserRepository;
import com.wishPot.Util.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    // Register User
    public void registerUser(RegistrationRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new RuntimeException("Username is already taken!");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setRole(request.getRole());

        userRepository.save(user);
    }

    // Authenticate User
    public AuthenticationResponse authenticateUser(LoginRequest request) {
        Optional<User> userOptional = userRepository.findByUsername(request.getUsername());

        if (userOptional.isEmpty() || !passwordEncoder.matches(request.getPassword(), userOptional.get().getPassword())) {
            throw new RuntimeException("Invalid username or password!");
        }

        User user = userOptional.get();

        // Generate JWT Token
        String token = jwtUtil.generateToken(user.getUsername());
        return new AuthenticationResponse(token);
    }

    // Get User Role
    public RoleResponse getUserRole(String username) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isEmpty()) {
            throw new RuntimeException("User not found!");
        }

        RoleResponse response = new RoleResponse();
        response.setUsername(username);
        response.setRole(userOptional.get().getRole());

        return response;
    }
}
