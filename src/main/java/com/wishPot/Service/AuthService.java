package com.wishPot.Service;

import com.wishPot.Dto.*;
import com.wishPot.Exception.UserAlreadyExistsException;
import com.wishPot.Exception.UserNotFoundException;
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

    // Check if email exists
    public boolean isEmailExists(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    // Check if username exists
    public boolean isUsernameExists(String username) {
        return userRepository.findByUsername(username).isPresent();
    }

    // Register User
    public void registerUser(RegistrationRequest request) {
        if (isUsernameExists(request.getUsername())) {
            throw new UserAlreadyExistsException("Username is already taken!");
        }
        if (isEmailExists(request.getEmail())) {
            throw new UserAlreadyExistsException("Email is already registered!");
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
            throw new UserNotFoundException("Invalid username or password!");
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
            throw new UserNotFoundException("User not found!");
        }

        RoleResponse response = new RoleResponse();
        response.setUsername(username);
        response.setRole(userOptional.get().getRole());

        return response;
    }
}
