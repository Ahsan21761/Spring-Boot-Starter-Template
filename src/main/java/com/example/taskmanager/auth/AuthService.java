package com.example.taskmanager.auth;

import com.example.taskmanager.auth.dto.AuthResponse;
import com.example.taskmanager.auth.dto.LoginRequest;
import com.example.taskmanager.auth.dto.RegisterRequest;
import com.example.taskmanager.common.exception.DuplicateResourceException;
import com.example.taskmanager.security.JwtService;
import com.example.taskmanager.user.Role;
import com.example.taskmanager.user.User;
import com.example.taskmanager.user.UserRepository;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Handles registration and login, issuing JWTs on success.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        String email = request.email().toLowerCase();
        if (userRepository.existsByEmail(email)) {
            throw new DuplicateResourceException("An account with this email already exists.");
        }

        User user = new User();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setFullName(request.fullName());
        user.setRole(Role.USER);
        user.setEnabled(true);
        userRepository.save(user);
        log.info("Registered new user id={}", user.getId());

        return issueToken(user);
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        String email = request.email().toLowerCase();
        // Delegates credential checking to Spring Security; throws BadCredentialsException on failure.
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, request.password()));

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("Authenticated user not found: " + email));
        return issueToken(user);
    }

    private AuthResponse issueToken(User user) {
        String token = jwtService.generateToken(
                user.getEmail(),
                Map.of("role", user.getRole().name(), "uid", user.getId()));
        return AuthResponse.bearer(token, jwtService.getExpirationMinutes());
    }
}
