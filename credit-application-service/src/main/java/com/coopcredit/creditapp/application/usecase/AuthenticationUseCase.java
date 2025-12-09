package com.coopcredit.creditapp.application.usecase;

import com.coopcredit.creditapp.application.dto.AuthResponse;
import com.coopcredit.creditapp.application.dto.LoginRequest;
import com.coopcredit.creditapp.application.dto.RegisterUserRequest;
import com.coopcredit.creditapp.domain.model.User;
import com.coopcredit.creditapp.domain.model.UserRole;
import com.coopcredit.creditapp.domain.port.UserRepositoryPort;
import com.coopcredit.creditapp.infrastructure.security.JwtTokenProvider;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Use case for user authentication and registration.
 */
@Service
public class AuthenticationUseCase {

    private final UserRepositoryPort userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final UserDetailsService userDetailsService;

    public AuthenticationUseCase(UserRepositoryPort userRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            JwtTokenProvider tokenProvider,
            UserDetailsService userDetailsService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
        this.userDetailsService = userDetailsService;
    }

    @Transactional
    public AuthResponse register(RegisterUserRequest request) {
        // Validate unique username and email
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        // Create user
        User user = new User(
                request.getUsername(),
                passwordEncoder.encode(request.getPassword()),
                request.getEmail());

        // Add role
        UserRole role = UserRole.valueOf(request.getRole());
        user.addRole(role);

        // Save user
        userRepository.save(user);

        // Generate token
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
        String token = tokenProvider.generateToken(userDetails);

        return new AuthResponse(token, tokenProvider.getExpirationTime(), user.getUsername());
    }

    public AuthResponse login(LoginRequest request) {
        try {
            // Authenticate
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()));

            // Generate token
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String token = tokenProvider.generateToken(userDetails);

            return new AuthResponse(token, tokenProvider.getExpirationTime(), userDetails.getUsername());

        } catch (BadCredentialsException e) {
            throw new IllegalArgumentException("Invalid username or password");
        }
    }
}
