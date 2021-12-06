package com.bookswap.bookswapapp.repositories;

import com.bookswap.bookswapapp.enums.EBookLabel;
import com.bookswap.bookswapapp.enums.ESwapStatus;
import com.bookswap.bookswapapp.models.Book;
import com.bookswap.bookswapapp.models.Swap;
import com.bookswap.bookswapapp.models.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class SwapRepositoryTest {

    @Autowired
    private SwapRepository testSwapRepository;
    @Autowired
    private UserRepository testUserRepository;
    @Autowired
    private BookRepository testBookRepository;

    @AfterEach
    void tearDown() {
        testSwapRepository.deleteAll();
        testBookRepository.deleteAll();
        testUserRepository.deleteAll();
    }

    @Test
    void testShouldFindUserSwaps() {
        User user1 = new User();
        user1.setUsername("username1");
        user1.setPassword("password");
        user1.setEmail("test1@gmail.com");
        testUserRepository.save(user1);
        User user2 = new User();
        user2.setUsername("username2");
        user2.setPassword("password");
        user2.setEmail("test2@gmail.com");
        testUserRepository.save(user2);

        EBookLabel label = EBookLabel.PERMANENT_SWAP;
        ESwapStatus status = ESwapStatus.IN_PROGRESS;

        Book book1 = new Book();
        book1.setUser(user1);
        book1.setLabel(label);
        testBookRepository.save(book1);
        Book book2 = new Book();
        book2.setUser(user2);
        book2.setLabel(label);
        testBookRepository.save(book2);

        Swap swap = new Swap();
        swap.setBook1(book1);
        swap.setBook2(book2);
        swap.setStatus(status);
        testSwapRepository.save(swap);

        List<ESwapStatus> statuses = new ArrayList<>();
        statuses.add(status);

        Optional<List<Swap>> expected = testSwapRepository.findUserSwaps(statuses, label, user1);

        assertThat(expected).isNotEmpty();
        assertThat(expected.get()).isNotNull();
        assertEquals(expected.get().size(), 1);
        assertEquals(expected.get().get(0), swap);
    }
}