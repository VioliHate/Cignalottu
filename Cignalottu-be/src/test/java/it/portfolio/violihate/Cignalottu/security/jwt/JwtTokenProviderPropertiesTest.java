package it.portfolio.violihate.cignalottu.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import net.jqwik.api.*;
import net.jqwik.api.constraints.StringLength;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.*;
import java.util.stream.Collectors;

class JwtTokenProviderPropertiesTest {

    /* SpringTest + properties = null
      The tests run before I get the values, so I wire them into the test class.
    */
    private static final String TEST_SECRET_BASE64 = "dGhpcy1pcy1hLXZlcnktbG9uZy1zZWNyZXQtZm9yLXRlc3RzLWFuZC10ZXN0aW5n";
    private static final long TEST_EXPIRATION_MS = 86400000L;

    /*  ────────────────────────────────────────────────────────────────
        Property 1: Generated token always has exactly 3 parts separated by dots
        (JWS Compact Serialization format: header.payload.signature)
        ──────────────────────────────────────────────────────────────── */
    @Property(tries = 100)
    boolean token_has_exactly_three_parts(
            @ForAll @StringLength(min = 3, max = 60) String username,
            @ForAll("roleSets") Set<String> roleNames
    ){
        Authentication auth = createAuthentication(username, roleNames);
        String token = createTestProvider().generateToken(auth);

        long dotCount = token.chars().filter(c -> c == '.').count();
        return dotCount == 2;
    }

    /* ────────────────────────────────────────────────────────────────
       Property 2: The 'sub' claim always matches the authenticated username
       ──────────────────────────────────────────────────────────────── */
    @Property(tries = 150)
    boolean subject_matches_username(@ForAll("validUsernames") String username) {
        Authentication auth = createAuthentication(username, Collections.emptySet());
        String token = createTestProvider().generateToken(auth);

        Claims claims = parseClaims(token);
        return username.equals(claims.getSubject());
    }

    /* ───────────────────────────────────────────────
       Property 3: Expiration is now + 24h (± 30 seconds)
       ─────────────────────────────────────────────── */
    @Property(tries = 50)
    boolean expiration_is_roughly_24_hours_ahead(@ForAll @StringLength(5) String dummy) {
        Authentication auth = createAuthentication("test@example.com", Collections.emptySet());
        String token = createTestProvider().generateToken(auth);

        Claims claims = parseClaims(token);
        Date exp = claims.getExpiration();
        Date now = new Date();

        long diffMs = exp.getTime() - now.getTime();
        long diffHours = diffMs / (1000 * 60 * 60);

        return diffHours >= 23 && diffHours <= 25;
    }

    /* ───────────────────────────────────────────────
       Property 4: The roles claim contains exactly the same roles
       that were present in the Authentication object
       ─────────────────────────────────────────────── */
    @Property(tries = 100)
    boolean roles_claim_matches_input_authorities(
            @ForAll("roleSets") Set<String> roleNames
    ) {
        Authentication auth = createAuthentication("user@test.it", roleNames);
        String token = createTestProvider().generateToken(auth);

        Claims claims = parseClaims(token);
        String rolesStr = claims.get("roles", String.class);

        if (roleNames.isEmpty()) {
            return rolesStr == null || rolesStr.trim().isEmpty();
        }

        Set<String> expected = roleNames.stream()
                .map(r -> "ROLE_" + r)
                .collect(Collectors.toSet());

        Set<String> actual = Arrays.stream(rolesStr.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toSet());

        return expected.equals(actual);
    }

    /* ───────────────────────────────────────────────
       Authentication Helper
       ───────────────────────────────────────────────*/
    private Authentication createAuthentication(String username, Set<String> roleNames) {
        Collection<GrantedAuthority> authorities = roleNames.stream()
                .map(r -> new SimpleGrantedAuthority("ROLE_" + r))
                .collect(Collectors.toList());

        return new UsernamePasswordAuthenticationToken(username, null, authorities);
    }

    // ───────────────────────────────────────────────
    // Parsing helper (JJWT 0.11.5)
    // ───────────────────────────────────────────────
    private Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(createTestProvider().getSecretKeyForTesting())  // if you add a public getter for testing
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /*
       ────────────────────────────────────────────────────────────────
       Arbitraries (value generators)
       ──────────────────────────────────────────────────────────────── */

    @Provide
    Arbitrary<String> validUsernames() {
        return Arbitraries.strings()
                .withCharRange('a', 'z')
                .withCharRange('A', 'Z')
                .withCharRange('0', '9')
                .withChars("@.-_")
                .ofMinLength(4)
                .ofMaxLength(80);
    }

    @Provide
    Arbitrary<Set<String>> roleSets() {
        return Arbitraries.of("CUSTOMER", "BARBER", "REPRESENTATIVE", "ADMIN")
                .set()
                .ofMaxSize(5)
                .ofMinSize(0);
    }

    // Creating provider fo Test
    private JwtTokenProvider createTestProvider() {
        JwtTokenProvider p = new JwtTokenProvider();
        ReflectionTestUtils.setField(p, "jwtSecretBase64", TEST_SECRET_BASE64);
        ReflectionTestUtils.setField(p, "jwtExpirationMs", TEST_EXPIRATION_MS);
        ReflectionTestUtils.invokeMethod(p, "init");
        return p;
    }
}