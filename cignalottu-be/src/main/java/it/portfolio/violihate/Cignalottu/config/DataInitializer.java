package it.portfolio.violihate.cignalottu.config;


import it.portfolio.violihate.cignalottu.entity.Provider;
import it.portfolio.violihate.cignalottu.entity.Role;
import it.portfolio.violihate.cignalottu.entity.User;
import it.portfolio.violihate.cignalottu.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    @Bean
    @Profile("dev")  // da eseguire solo in configurazione dev
    CommandLineRunner initTestUsers(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {

        return args -> {
            log.info("Inizializzazione utenti di test...");

            createUserIfNotExists(
                    userRepository, passwordEncoder,
                    "admin@cignalottu.it",
                    "admin123",
                    "Admin",
                    "Super",
                    Role.ADMIN
            );

            createUserIfNotExists(
                    userRepository, passwordEncoder,
                    "barber@test.it",
                    "barber123",
                    "Luca",
                    "Bianchi",
                    Role.BARBER
            );

            createUserIfNotExists(
                    userRepository, passwordEncoder,
                    "cliente@test.it",
                    "cliente123",
                    "Mario",
                    "Rossi",
                    Role.CUSTOMER
            );

            createUserIfNotExists(
                    userRepository,
                    passwordEncoder,
                    "rappresentante@test.it",
                    "rapp123",
                    "Giulia",
                    "Verdi",
                    Role.REPRESENTATIVE
            );

            log.info("Utenti di test inizializzati con successo.");
        };
    }

    private void createUserIfNotExists(
            UserRepository repo,
            PasswordEncoder encoder,
            String email,
            String rawPassword,
            String firstName,
            String lastName,
            Role role) {

        if (repo.existsByEmail(email)) {
            log.info("Utente {} già presente, salto creazione.", email);
            return;
        }
        User user = new User(email, encoder.encode(rawPassword), firstName, lastName, role, Provider.LOCAL, null);
        repo.save(user);
    }
}
