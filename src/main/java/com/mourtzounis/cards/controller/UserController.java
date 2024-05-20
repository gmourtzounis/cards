package com.mourtzounis.cards.controller;

import com.mourtzounis.cards.dto.UserDto;
import com.mourtzounis.cards.dto.request.LoginRequest;
import com.mourtzounis.cards.dto.request.RegisterRequest;
import com.mourtzounis.cards.dto.response.ApiResponse;
import com.mourtzounis.cards.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/users")
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse> register(@RequestBody @Valid RegisterRequest registerRequest) {
        userService.createUser(registerRequest);
        ApiResponse apiResponse = new ApiResponse("User was successfully created.");

        return new ResponseEntity<>(apiResponse, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<UserDto> authenticateAndGetToken(@RequestBody @Valid LoginRequest loginRequest) {
        return userService.authenticateUser(loginRequest);
    }
    @GetMapping("/{userId}/profile")
    @PreAuthorize("hasAnyAuthority('ROLE_MEMBER', 'ROLE_ADMIN') and #userId == authentication.principal.id")
    public ResponseEntity<UserDto> userProfile(@Valid @NotNull(message = "userId cannot be null") @PathVariable Long userId) {

        return ResponseEntity.ok(userService.loadUserDto(userId));
    }

}