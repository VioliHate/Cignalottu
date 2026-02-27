package it.portfolio.violihate.cignalottu.service;

import it.portfolio.violihate.cignalottu.dto.response.AuthResponse;
import it.portfolio.violihate.cignalottu.entity.Provider;
import it.portfolio.violihate.cignalottu.entity.Role;
import it.portfolio.violihate.cignalottu.entity.User;
import it.portfolio.violihate.cignalottu.repository.UserRepository;
import it.portfolio.violihate.cignalottu.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
public class OAuth2UserServiceImpl implements OAuth2UserProcessor{

    private final UserRepository userRepository;
    private final JwtService jwtService;

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$");


    @Override
    @Transactional
    public AuthResponse processOAuth2User(OAuth2User oauthUser) {
        String normalizedEmail = Objects.requireNonNull(oauthUser.getAttribute("email")).toString().trim().toLowerCase();
        String googleId = oauthUser.getAttribute("sub");
        if (!EMAIL_PATTERN.matcher(normalizedEmail).matches()) {
            throw new IllegalArgumentException("Email non valida da Google");
        }

        String firstName = capitalize(oauthUser.getAttribute("given_name"));
        String lastName = capitalize(oauthUser.getAttribute("family_name"));


        User user = userRepository.findByEmail(normalizedEmail)
                .orElseGet(() -> {
                    User newUser = new User();
                    newUser.setEmail(normalizedEmail);
                    newUser.setPassword(null);
                    newUser.setFirstName(firstName);
                    newUser.setLastName(lastName);
                    newUser.setRole(Role.CUSTOMER); // da vedere il ruolo
                    newUser.setProvider(Provider.GOOGLE);
                    newUser.setProviderId(googleId);
                    newUser.setCreatedAt(LocalDateTime.now());
                    newUser.setUpdatedAt(LocalDateTime.now());
                    return userRepository.save(newUser);
                });

        if (!Provider.GOOGLE.equals(user.getProvider())) {
            log.info("Merge account: {} era {}, ora GOOGLE", normalizedEmail, user.getProvider());
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

        log.debug("OAuth2 token generati per: {}", normalizedEmail);

        return new AuthResponse(
                accessToken,
                refreshToken,
                "Bearer",
                user.getId(),
                user.getEmail(),
                user.getRole().name());
    }

    //utility
    private String capitalize(String str) {
        if (str == null || str.isEmpty()) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }
}
