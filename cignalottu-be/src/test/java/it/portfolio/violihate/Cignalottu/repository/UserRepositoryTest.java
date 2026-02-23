package it.portfolio.violihate.cignalottu.repository;

import it.portfolio.violihate.cignalottu.entity.Role;
import it.portfolio.violihate.cignalottu.entity.User;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldSaveAndFindUserByEmail() {

        User user = createTestUser("mario.rossi@example.com", Role.CUSTOMER);

        entityManager.persistAndFlush(user);

        boolean exists = userRepository.existsByEmail("mario.rossi@example.com");

        assertThat(exists).isTrue();
    }

    @Test
    void shouldNotFindNonExistingEmail() {
        boolean exists = userRepository.existsByEmail("inesistente@example.com");
        assertThat(exists).isFalse();
    }

    @Test
    void shouldFindUserByEmail() {

        User user = entityManager.persistFlushFind(createTestUser("test@example.com", Role.CUSTOMER));
        User found = userRepository.findByEmail("test@example.com").orElse(null);

        Assertions.assertNotNull(found);
        assertThat(found.getRole()).isEqualTo(user.getRole());
        assertThat(found.getEmail()).isEqualTo("test@example.com");
    }

    @Test
    void shouldReturnEmptyWhenEmailNotFound() {
        Optional<User> found = userRepository.findByEmail("not-exist@example.com");
        assertThat(found).isEmpty();
    }

    @Test
    void shouldPreventDuplicateEmail() {
        User user1 = createTestUser("duplicate@example.com", Role.CUSTOMER);
        entityManager.persistAndFlush(user1);

        User user2 = createTestUser("duplicate@example.com", Role.BARBER);

        //deve lanciare exception perchè email è unique
        assertThrows(ConstraintViolationException.class, () -> {
            entityManager.persistAndFlush(user2);
        });
    }


    // metodo per creare al volo un test user data una email e un ruolo
    private User createTestUser(String email, Role role) {
        User u = new User();
        u.setEmail(email);
        u.setFirstName("Test");
        u.setLastName("User");
        u.setPassword("hashed");
        u.setRole(role);
        return u;
    }
}
