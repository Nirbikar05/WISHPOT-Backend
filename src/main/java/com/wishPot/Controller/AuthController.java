package com.wishPot.Controller;

import com.wishPot.Dto.*;
import com.wishPot.Exception.UserAlreadyExistsException;
import com.wishPot.Service.AuthService;
import com.wishPot.Service.OTPService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * Controller for Authentication and User Management APIs.
 */
@Slf4j
@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final OTPService otpService;

    /**
     * Endpoint for user registration. Sends OTP for email verification.
     *
     * @param request RegistrationRequest object containing user details.
     * @return ResponseEntity with registration status.
     */
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody @Valid RegistrationRequest request) {
        log.info("Initiating registration process for email: {}", request.getEmail());

        if (authService.isEmailExists(request.getEmail())) {
            log.warn("Email already exists: {}", request.getEmail());
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Email already exists. Please use another email.");
        }

        try {
            String otp = otpService.generateOTP(request.getEmail());
            otpService.sendOTP(request.getEmail(), otp);
            log.info("OTP sent to email: {}", request.getEmail());
            return ResponseEntity.ok("OTP sent to your email for verification. Please check your inbox.");
        } catch (Exception e) {
            log.error("Error during registration for email: {}", request.getEmail(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while processing your registration. Please try again later.");
        }
    }

    /**
     * Endpoint for OTP verification. Completes user registration upon successful OTP validation.
     *
     * @param otpRequest OTPVerificationRequest object containing email, OTP, and user details.
     * @return ResponseEntity with registration confirmation or failure response.
     */
    @PostMapping("/verify-otp")
    public ResponseEntity<String> verifyOtp(@RequestBody @Valid OTPVerificationRequest otpRequest) {
        log.info("Initiating OTP verification for email: {}", otpRequest.getEmail());

        // Step 1: Verify OTP
        if (!otpService.verifyOTP(otpRequest.getEmail(), otpRequest.getOtp())) {
            log.warn("Invalid or expired OTP for email: {}", otpRequest.getEmail());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Invalid or expired OTP. Please request a new OTP.");
        }

        // Step 2: Attempt user registration
        try {
            // Create registration request from OTP verification request
            RegistrationRequest registrationRequest = new RegistrationRequest(
                    otpRequest.getEmail(),
                    otpRequest.getPassword(),
                    otpRequest.getUsername()
            );

            authService.registerUser(registrationRequest);
            log.info("User successfully registered with email: {}", otpRequest.getEmail());
            return ResponseEntity.ok("User registered successfully!");
        }
        // Step 3: Handle specific exceptions (if any)
        catch (UserAlreadyExistsException e) {
            log.error("Registration failed - User already exists: {}", otpRequest.getEmail(), e);
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("User with this email already exists. Please login or use a different email.");
        } catch (Exception e) {
            log.error("Unexpected error during user registration for email: {}", otpRequest.getEmail(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("User registration failed due to an unexpected error. Please try again later.");
        }
    }


    /**
     * Endpoint for user login.
     *
     * @param request LoginRequest object containing login credentials.
     * @return ResponseEntity with AuthenticationResponse on successful authentication.
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid LoginRequest request) {
        log.info("Processing login for username: {}", request.getUsername());
        try {
            AuthenticationResponse response = authService.authenticateUser(request);
            log.info("User authenticated successfully: {}", request.getUsername());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Login failed for username: {}", request.getUsername(), e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid login credentials. Please try again.");
        }
    }

    /**
     * Endpoint to retrieve user role based on username.
     *
     * @param username The username of the user.
     * @return ResponseEntity with user role details.
     */
    @GetMapping("/role/{username}")
    public ResponseEntity<?> getRole(@PathVariable String username) {
        log.info("Fetching role for username: {}", username);
        try {
            RoleResponse response = authService.getUserRole(username);
            log.info("Role retrieved successfully for username: {}", username);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error fetching role for username: {}", username, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User role not found. Please check the username.");
        }
    }
}
