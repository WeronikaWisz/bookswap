package com.bookswap.bookswapapp.repositories;

import com.bookswap.bookswapapp.models.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository testUserRepository;

    @AfterEach
    void tearDown() {
        testUserRepository.deleteAll();
    }

    @Test
    void testShouldExistsByEmail() {
        String email = "test@gmail.com";
        User user = new User();
        user.setUsername("username");
        user.setPassword("password");
        user.setEmail(email);
        testUserRepository.save(user);

        boolean expected = testUserRepository.existsByEmail(email);

        assertThat(expected).isTrue();
    }

    @Test
    void testShouldNotExistsByEmail() {
        String email = "test@gmail.com";

        boolean expected = testUserRepository.existsByEmail(email);

        assertThat(expected).isFalse();
    }

    @Test
    void testShouldExistsByUsername() {
        String username = "username";
        User user = new User();
        user.setUsername(username);
        user.setPassword("password");
        user.setEmail("test@gmail.com");
        testUserRepository.save(user);

        boolean expected = testUserRepository.existsByUsername(username);

        assertThat(expected).isTrue();
    }

    @Test
    void testShouldNotExistsByUsername() {
        String username = "username";

        boolean expected = testUserRepository.existsByUsername(username);

        assertThat(expected).isFalse();
    }

    @Test
    void testShouldFindByUsername() {
        String username = "username";
        User user = new User();
        user.setUsername(username);
        user.setPassword("password");
        user.setEmail("test@gmail.com");
        testUserRepository.save(user);

        Optional<User> expectedUser = testUserRepository.findByUsername(username);

        assertThat(expectedUser).isNotEmpty();
        assertThat(expectedUser.get()).isNotNull();
        assertEquals(expectedUser.get().getUsername(), username);

    }

    @Test
    void testShouldNotFindByUsername() {
        String username = "username";

        Optional<User> expectedUser = testUserRepository.findByUsername(username);

        assertThat(expectedUser).isEmpty();

    }
}