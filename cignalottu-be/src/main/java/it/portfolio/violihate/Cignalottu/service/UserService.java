package it.portfolio.violihate.cignalottu.service;

import it.portfolio.violihate.cignalottu.dto.request.RegisterRequest;
import it.portfolio.violihate.cignalottu.dto.response.RegisterResponse;
import it.portfolio.violihate.cignalottu.entity.Role;
import it.portfolio.violihate.cignalottu.entity.User;
import it.portfolio.violihate.cignalottu.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;



    @Transactional
    public User registerUser(RegisterRequest dto) {

        String normalizedEmail = dto.email().trim().toLowerCase();
        if (userRepository.existsByEmail(normalizedEmail)) {
            throw new IllegalArgumentException("Email: " + normalizedEmail +" già in uso");
        }

        User user = new User();
        user.setEmail(normalizedEmail);
        user.setPassword(passwordEncoder.encode(dto.password()));
        user.setFirstName(dto.firstName());
        user.setLastName(dto.lastName());
        user.setRole(Role.CUSTOMER);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        return userRepository.save(user);
    }


    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utente non trovato con email: " + email));
    }

    public RegisterResponse toRegisterResponse(User user) {
        return new RegisterResponse(user);
    }
}
