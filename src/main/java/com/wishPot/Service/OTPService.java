package com.wishPot.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
public class OTPService {

    private final Map<String, String> otpStore = new HashMap<>();
    private final Map<String, Long> otpExpiry = new HashMap<>();
    private final long OTP_EXPIRY_DURATION = TimeUnit.MINUTES.toMillis(5);

    @Autowired
    private JavaMailSender mailSender;

    public String generateOTP(String email) {
        String otp = String.format("%06d", new Random().nextInt(999999));
        otpStore.put(email, otp);
        otpExpiry.put(email, System.currentTimeMillis() + OTP_EXPIRY_DURATION);
        return otp;
    }

    public void sendOTP(String email, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Your OTP Code");
        message.setText("Your OTP code is: " + otp + "\nIt will expire in 5 minutes.");

        // Send the email
        mailSender.send(message);

        System.out.println("OTP sent successfully to email: " + email);
    }

    public boolean verifyOTP(String email, String otp) {
        if (!otpStore.containsKey(email)) {
            return false;
        }

        String storedOtp = otpStore.get(email);
        Long expiryTime = otpExpiry.get(email);

        if (System.currentTimeMillis() > expiryTime) {
            otpStore.remove(email);
            otpExpiry.remove(email);
            return false; // OTP expired
        }

        if (!storedOtp.equals(otp)) {
            return false; // Invalid OTP
        }

        otpStore.remove(email);
        otpExpiry.remove(email);
        return true; // OTP verified
    }
}
