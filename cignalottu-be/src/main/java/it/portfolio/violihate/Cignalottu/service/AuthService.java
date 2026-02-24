package it.portfolio.violihate.cignalottu.service;

import it.portfolio.violihate.cignalottu.dto.request.LoginRequest;
import it.portfolio.violihate.cignalottu.dto.request.RegisterRequest;
import it.portfolio.violihate.cignalottu.dto.response.AuthResponse;
import it.portfolio.violihate.cignalottu.dto.response.RegisterResponse;
import it.portfolio.violihate.cignalottu.entity.Provider;
import it.portfolio.violihate.cignalottu.entity.Role;
import it.portfolio.violihate.cignalottu.entity.User;
import it.portfolio.violihate.cignalottu.repository.UserRepository;
import it.portfolio.violihate.cignalottu.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserService userService;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    private final UserRepository userRepository;

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$");


    @Transactional
    public AuthResponse register(RegisterRequest request) {
        validateRegistrationInput(request);

        User savedUser = userService.registerUser(request);

        UserDetailsImpl userDetails = new UserDetailsImpl(savedUser);

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        );

        String accessToken = jwtService.generateAccessToken(authentication);
        String refreshToken = jwtService.generateRefreshToken(authentication);

        log.info("Registrazione e auto-login completati per: {}", savedUser.getEmail().trim().toLowerCase());

        return buildAuthResponse(accessToken, refreshToken, savedUser);
    }


    public AuthResponse login(LoginRequest request) {
        String normalizedEmail = request.email().trim().toLowerCase();

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            normalizedEmail,
                            request.password()
                    )
            );

            UserDetailsImpl principal = (UserDetailsImpl) authentication.getPrincipal();
            User user = principal.user();

            if (Provider.GOOGLE.equals(user.getProvider())) {
                throw new BadCredentialsException("Account registrato con Google. Usa Google.");
            }

            String accessToken = jwtService.generateAccessToken(authentication);
            String refreshToken = jwtService.generateRefreshToken(authentication);

            log.info("Login riuscito per: {}", normalizedEmail);

            return buildAuthResponse(accessToken, refreshToken, user);

        } catch (BadCredentialsException e) {
            log.warn("Tentativo di login fallito");
            throw new BadCredentialsException("Credenziali non valide");
        }
    }


    @Transactional(readOnly = true)
    public AuthResponse refresh(String refreshToken) {
        String email = jwtService.extractEmail(refreshToken);

        if (email == null || !jwtService.isTokenValid(refreshToken, email)) {
            throw new BadCredentialsException("Refresh token non valido o scaduto");
        }

        User user = userService.findByEmail(email);
        UserDetailsImpl userDetails = new UserDetailsImpl(user);

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        );
        String newAccessToken = jwtService.generateAccessToken(authentication);

        return buildAuthResponse(newAccessToken, refreshToken, user);
    }


    @Transactional
    public AuthResponse processOAuth2User(String email, String name, String googleId) {
        String normalizedEmail = email.trim().toLowerCase();

        User user = userRepository.findByEmail(normalizedEmail)
                .orElseGet(() -> {
                    User newUser = new User();
                    newUser.setEmail(normalizedEmail);
                    newUser.setPassword(null);
                    newUser.setFirstName(name != null ? name.trim() : "Utente Google");
                    newUser.setLastName("");
                    newUser.setRole(Role.CUSTOMER);
                    newUser.setProvider(Provider.GOOGLE);
                    newUser.setProviderId(googleId);
                    newUser.setCreatedAt(LocalDateTime.now());
                    newUser.setUpdatedAt(LocalDateTime.now());
                    return userRepository.save(newUser);
                });

        if (!Provider.GOOGLE.equals(user.getProvider())) {
            user.setProvider(Provider.GOOGLE);
            user.setProviderId(googleId);
            user.setUpdatedAt(LocalDateTime.now());
            user = userRepository.save(user);
        }

        UserDetailsImpl userDetails = new UserDetailsImpl(user);
        Authentication auth = new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        );

        String accessToken = jwtService.generateAccessToken(auth);
        String refreshToken = jwtService.generateRefreshToken(auth);

        return buildAuthResponse(accessToken, refreshToken, user);
    }


    private void validateRegistrationInput(RegisterRequest req) {
        if (req.email() == null || !EMAIL_PATTERN.matcher(req.email()).matches()) {
            throw new IllegalArgumentException("Formato email non valido");
        }

        if (req.password() == null || req.password().length() < 8) {
            throw new IllegalArgumentException("La password deve avere almeno 8 caratteri");
        }

        if (!req.password().matches(".*[A-Z].*")) {
            throw new IllegalArgumentException("La password deve contenere almeno una lettera maiuscola");
        }

        if (!req.password().matches(".*[a-z].*")) {
            throw new IllegalArgumentException("La password deve contenere almeno una lettera minuscola");
        }

        if (!req.password().matches(".*\\d.*")) {
            throw new IllegalArgumentException("La password deve contenere almeno un numero");
        }
    }

    private AuthResponse buildAuthResponse(String accessToken, String refreshToken, User user) {
        return new AuthResponse(
                accessToken,
                refreshToken,
                "Bearer",
                user.getId(),
                user.getEmail(),
                user.getRole().name()
        );
    }
}
