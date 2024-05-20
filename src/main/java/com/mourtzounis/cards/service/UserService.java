package com.mourtzounis.cards.service;

import com.mourtzounis.cards.dto.request.LoginRequest;
import com.mourtzounis.cards.dto.UserDto;
import com.mourtzounis.cards.model.UserInfo;
import com.mourtzounis.cards.dto.request.RegisterRequest;
import org.springframework.http.ResponseEntity;

public interface UserService {
    void createUser(RegisterRequest registerRequest);

    ResponseEntity<UserDto> authenticateUser(LoginRequest loginRequest);

    UserInfo loadUser(Long userId);

    UserDto loadUserDto(Long userId);
}
