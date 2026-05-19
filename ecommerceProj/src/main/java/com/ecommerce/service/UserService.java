package com.ecommerce.service;

import com.ecommerce.Security.JwtUtil;
import com.ecommerce.dto.UpdatePasswordRequest;
import com.ecommerce.dto.UpdateProfileRequest;
import com.ecommerce.dto.UserProfileResponse;
import com.ecommerce.entity.User;
import com.ecommerce.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class UserService {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserService(
        JwtUtil jwtUtil,
        UserRepository userRepository,
        BCryptPasswordEncoder passwordEncoder
    ) {
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostConstruct
    public void init() {
        if (userRepository.count() == 0) {
            User user = new User();
            user.setName("Test User");
            user.setEmail("test@example.com");
            user.setPassword(passwordEncoder.encode("password"));
            user.setPhone("1234567890");
            user.setAddress("Test Address, India");
            userRepository.save(user);
        }
    }

    public User registerUser(User user) {
        Optional<User> existingUser = userRepository.findByEmail(user.getEmail());
        if (existingUser.isPresent()) {
            throw new IllegalArgumentException("Email already registered");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public Map<String, Object> loginUser(String email, String password) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("Invalid password");
        }

        Map<String, Object> response = new HashMap<>();
        response.put("token", jwtUtil.generateToken(email));
        response.put("userId", user.getUserId());
        return response;
    }

    public UserProfileResponse getMyProfile(Authentication authentication) {
        User user = getCurrentUser(authentication);
        return toProfileResponse(user);
    }

    public UserProfileResponse updateProfile(Authentication authentication, UpdateProfileRequest request) {
        User user = getCurrentUser(authentication);
        user.setName(request.getName().trim());
        user.setPhone(request.getPhone().trim());
        user.setAddress(request.getAddress().trim());
        userRepository.save(user);
        return toProfileResponse(user);
    }

    public UserProfileResponse updatePassword(Authentication authentication, UpdatePasswordRequest request) {
        User user = getCurrentUser(authentication);

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Old password is incorrect.");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        return toProfileResponse(user);
    }

    public void logout(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }
    }

    private User getCurrentUser(Authentication authentication) {
        if (authentication == null || authentication.getName() == null || authentication.getName().isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }

        return userRepository.findByEmail(authentication.getName())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }

    private UserProfileResponse toProfileResponse(User user) {
        return new UserProfileResponse(
            user.getUserId(),
            user.getName(),
            user.getEmail(),
            user.getPhone(),
            user.getAddress()
        );
    }
}
