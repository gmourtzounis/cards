package com.mourtzounis.cards.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mourtzounis.cards.dto.UserDto;
import com.mourtzounis.cards.dto.request.LoginRequest;
import com.mourtzounis.cards.dto.request.RegisterRequest;
import com.mourtzounis.cards.model.UserInfo;
import com.mourtzounis.cards.repository.UserInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private static final String BEARER = "Bearer ";
    private final UserInfoRepository repository;
    private final PasswordEncoder encoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserInfoRepository userInfoRepository;
    private final ObjectMapper mapper;

    @Override
    public void createUser(RegisterRequest registerRequest) {
        try {
            registerRequest.setPassword(encoder.encode(registerRequest.getPassword()));
            UserInfo userInfo = mapper.convertValue(registerRequest, UserInfo.class);

            repository.save(userInfo);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException("Email already in use");
        }
    }

    @Override
    public ResponseEntity<UserDto> authenticateUser(LoginRequest loginRequest) {
        var authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

        var userDetails = (UserDetailsImpl) authentication.getPrincipal();

        if (authentication.isAuthenticated()) {
            var jwtToken = jwtService.generateToken(loginRequest.getEmail());
            var userInfo = new UserInfo(
                    userDetails.getId(),
                    userDetails.getEmail(),
                    userDetails.getPassword(),
                    userDetails.getCards(),
                    userDetails.getAuthorities().toString()
            );

            var headers = new HttpHeaders();
            headers.set(HttpHeaders.AUTHORIZATION, BEARER + jwtToken);
            var userDto = mapper.convertValue(userInfo, UserDto.class);

            return ResponseEntity.ok().headers(headers).body(userDto);

        } else {
            throw new UsernameNotFoundException("invalid user request!");
        }
    }

    @Override
    public UserInfo loadUser(Long userId) {
        return userInfoRepository.findById(userId).orElseThrow(() -> new UsernameNotFoundException("invalid user"));
    }

    @Override
    public UserDto loadUserDto(Long userId) {
        UserInfo userInfo = userInfoRepository.findById(userId).orElseThrow(() -> new UsernameNotFoundException("invalid user"));

        return mapper.convertValue(userInfo, UserDto.class);

    }
}
