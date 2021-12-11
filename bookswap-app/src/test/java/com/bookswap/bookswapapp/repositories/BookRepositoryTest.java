package com.bookswap.bookswapapp.repositories;

import com.bookswap.bookswapapp.enums.EBookLabel;
import com.bookswap.bookswapapp.enums.EBookStatus;
import com.bookswap.bookswapapp.models.Book;
import com.bookswap.bookswapapp.models.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class BookRepositoryTest {

    @Autowired
    private BookRepository testBookRepository;
    @Autowired
    private UserRepository testUserRepository;

    @AfterEach
    void tearDown() {
        testBookRepository.deleteAll();
        testUserRepository.deleteAll();
    }

    @Test
    void testCountUserAvailableBooks() {
        User user1 = new User();
        user1.setUsername("username1");
        user1.setPassword("password");
        user1.setEmail("test1@gmail.com");
        testUserRepository.save(user1);

        EBookLabel label = EBookLabel.PERMANENT_SWAP;
        EBookStatus status = EBookStatus.AVAILABLE;

        Book book1 = new Book();
        book1.setUser(user1);
        book1.setLabel(label);
        book1.setStatus(status);
        testBookRepository.save(book1);

        long actual = testBookRepository.countUserAvailableBooks(label, user1);

        assertEquals(1, actual);
    }
}