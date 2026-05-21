package com.ecommerce.controller;

import com.ecommerce.dto.ApiResponse;
import com.ecommerce.dto.LoginRequest;
import com.ecommerce.dto.UpdatePasswordRequest;
import com.ecommerce.dto.UpdateProfileRequest;
import com.ecommerce.dto.UserProfileResponse;
import com.ecommerce.entity.User;
import com.ecommerce.service.UserService;
import jakarta.validation.Valid;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserProfileResponse>> registerUser(@Valid @RequestBody User user) {
        return ResponseEntity.ok(ApiResponse.success("Registration successful.", userService.registerUser(user)));
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> loginUser(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(userService.loginUser(request.getEmail(), request.getPassword()));
    }

    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<String>> dashboard() {
        return ResponseEntity.ok(ApiResponse.success("Protected API accessed.", "Protected API Accessed!"));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserProfileResponse>> getMyProfile(Authentication authentication) {
        return ResponseEntity.ok(ApiResponse.success("Profile fetched successfully.", userService.getMyProfile(authentication)));
    }

    @PutMapping("/update-profile")
    public ResponseEntity<ApiResponse<UserProfileResponse>> updateProfile(
        Authentication authentication,
        @Valid @RequestBody UpdateProfileRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success("Profile updated successfully.", userService.updateProfile(authentication, request)));
    }

    @PutMapping("/update-password")
    public ResponseEntity<ApiResponse<UserProfileResponse>> updatePassword(
        Authentication authentication,
        @Valid @RequestBody UpdatePasswordRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success("Password updated successfully.", userService.updatePassword(authentication, request)));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Map<String, String>>> logout(Authentication authentication) {
        userService.logout(authentication);
        return ResponseEntity.ok(ApiResponse.success("Logged out successfully.", Map.of("message", "Logged out successfully")));
    }
}
