package com.bookswap.bookswapapp.repositories;

import com.bookswap.bookswapapp.enums.EBookLabel;
import com.bookswap.bookswapapp.enums.EBookStatus;
import com.bookswap.bookswapapp.enums.ERequestStatus;
import com.bookswap.bookswapapp.models.Book;
import com.bookswap.bookswapapp.models.SwapRequest;
import com.bookswap.bookswapapp.models.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class SwapRequestRepositoryTest {

    @Autowired
    private SwapRequestRepository testSwapRequestRepository;
    @Autowired
    private UserRepository testUserRepository;
    @Autowired
    private BookRepository testBookRepository;
    private User user1;
    private User user2;
    private final EBookLabel label = EBookLabel.PERMANENT_SWAP;

    @BeforeEach
    void setUp() {
        user1 = new User();
        user1.setUsername("username1");
        user1.setPassword("password");
        user1.setEmail("test1@gmail.com");
        testUserRepository.save(user1);
        user2 = new User();
        user2.setUsername("username2");
        user2.setPassword("password");
        user2.setEmail("test2@gmail.com");
        testUserRepository.save(user2);

        EBookStatus status = EBookStatus.AVAILABLE;

        Book book1 = new Book();
        book1.setUser(user2);
        book1.setLabel(label);
        book1.setStatus(status);
        testBookRepository.save(book1);

        SwapRequest swapRequest = new SwapRequest();
        swapRequest.setUser(user1);
        swapRequest.setStatus(ERequestStatus.WAITING);
        swapRequest.setBook(book1);
        testSwapRequestRepository.save(swapRequest);
    }

    @AfterEach
    void tearDown() {
        testSwapRequestRepository.deleteAll();
        testBookRepository.deleteAll();
        testUserRepository.deleteAll();
    }

    @Test
    void countUserOfferRequest() {

        long actual = testSwapRequestRepository.countUserOfferRequest(label, user1);
        assertEquals(1, actual);
    }

    @Test
    void findOffersSendFromUser() {
        Optional<List<SwapRequest>> offersSendFromUser = testSwapRequestRepository.findOffersSendFromUser(
                user1, user2, label);
        assertThat(offersSendFromUser).isNotEmpty();
        assertEquals(1, offersSendFromUser.get().size());
    }

    @Test
    void userSendRequestsBooks() {
        Optional<List<Long>> sendRequestList = testSwapRequestRepository.userSendRequestsBooks(label, user1);
        assertThat(sendRequestList).isNotEmpty();
        assertEquals(1, sendRequestList.get().size());
    }
}