package com.bookswap.bookswapapp.services;

import com.bookswap.bookswapapp.dtos.bookswaps.SwapFilter;
import com.bookswap.bookswapapp.dtos.bookswaps.SwapListItem;
import com.bookswap.bookswapapp.dtos.manageusers.UpdateUserData;
import com.bookswap.bookswapapp.enums.EBookLabel;
import com.bookswap.bookswapapp.enums.EBookStatus;
import com.bookswap.bookswapapp.enums.ESwapStatus;
import com.bookswap.bookswapapp.exception.ApiNotFoundException;
import com.bookswap.bookswapapp.models.Book;
import com.bookswap.bookswapapp.models.Swap;
import com.bookswap.bookswapapp.models.User;
import com.bookswap.bookswapapp.repositories.BookRepository;
import com.bookswap.bookswapapp.repositories.SwapRepository;
import com.bookswap.bookswapapp.repositories.UserRepository;
import com.bookswap.bookswapapp.security.userdetails.UserDetailsI;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class BookSwapsServiceTest {

    @Mock
    private UserRepository testUserRepository;
    @Mock
    private SwapRepository testSwapRepository;
    @Mock
    private BookRepository testBookRepository;
    private BookSwapsService testBookSwapsService;
    private User user;

    @BeforeEach
    void setUp() {
        testBookSwapsService = new BookSwapsService(testUserRepository, testSwapRepository);
    }

    @Nested
    class TestWithUser{

        @BeforeEach
        public void init() {
            user = new User();
            user.setUsername("username");
            user.setPassword("password");
            user.setEmail("test1@gmail.com");
            testUserRepository.save(user);
            UserDetailsI applicationUser = UserDetailsI.build(user);
            Authentication authentication = Mockito.mock(Authentication.class);
            SecurityContext securityContext = Mockito.mock(SecurityContext.class);
            Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
            SecurityContextHolder.setContext(securityContext);
            Mockito.when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(applicationUser);
        }

        @Test
        void testGetSwaps() {
            EBookLabel label = EBookLabel.PERMANENT_SWAP;

            SwapFilter swapFilter = new SwapFilter();
            swapFilter.setBookLabel(label);
            List<ESwapStatus> statuses = new ArrayList<>();
            statuses.add(ESwapStatus.IN_PROGRESS);
            swapFilter.setSwapStatus(statuses);

            User user2 = new User();
            user2.setUsername("username2");
            user2.setPassword("password");
            user2.setEmail("test2@gmail.com");
            testUserRepository.save(user2);

            Book book1 = new Book();
            book1.setUser(user);
            book1.setLabel(label);
            testBookRepository.save(book1);
            Book book2 = new Book();
            book2.setUser(user2);
            book2.setLabel(label);
            testBookRepository.save(book2);

            Swap swap = new Swap();
            swap.setStatus(ESwapStatus.IN_PROGRESS);
            swap.setBook1(book1);
            swap.setBook2(book2);
            testSwapRepository.save(swap);

            Mockito.when(testUserRepository.findByUsername("username")).thenReturn(java.util.Optional.of(user));
            Mockito.when(testSwapRepository.findUserSwaps(swapFilter.getSwapStatus(), swapFilter.getBookLabel(), user))
                    .thenReturn(Optional.of(List.of(swap)));

            List<SwapListItem> swaps = testBookSwapsService.getSwaps(swapFilter);

            verify(testSwapRepository).findUserSwaps(swapFilter.getSwapStatus(), swapFilter.getBookLabel(), user);
            assertEquals(1, swaps.size());
            assertEquals(swap.getId(), swaps.get(0).getId());
        }

        @Test
        void testGetSwapsOtherUserBook1() {
            EBookLabel label = EBookLabel.PERMANENT_SWAP;

            SwapFilter swapFilter = new SwapFilter();
            swapFilter.setBookLabel(label);
            List<ESwapStatus> statuses = new ArrayList<>();
            statuses.add(ESwapStatus.IN_PROGRESS);
            swapFilter.setSwapStatus(statuses);

            User user2 = new User();
            user2.setUsername("username2");
            user2.setPassword("password");
            user2.setEmail("test2@gmail.com");
            testUserRepository.save(user2);

            Book book1 = new Book();
            book1.setUser(user2);
            book1.setLabel(label);
            testBookRepository.save(book1);
            Book book2 = new Book();
            book2.setUser(user);
            book2.setLabel(label);
            testBookRepository.save(book2);

            Swap swap = new Swap();
            swap.setStatus(ESwapStatus.IN_PROGRESS);
            swap.setBook1(book1);
            swap.setBook2(book2);
            testSwapRepository.save(swap);

            Mockito.when(testUserRepository.findByUsername("username")).thenReturn(java.util.Optional.of(user));
            Mockito.when(testSwapRepository.findUserSwaps(swapFilter.getSwapStatus(), swapFilter.getBookLabel(), user))
                    .thenReturn(Optional.of(List.of(swap)));

            List<SwapListItem> swaps = testBookSwapsService.getSwaps(swapFilter);

            verify(testSwapRepository).findUserSwaps(swapFilter.getSwapStatus(), swapFilter.getBookLabel(), user);
            assertEquals(1, swaps.size());
            assertEquals(swap.getId(), swaps.get(0).getId());
        }

        @Test
        void testGetSwapsUserNotFound() {
            Mockito.when(testUserRepository.findByUsername("username")).thenReturn(java.util.Optional.empty());
            SwapFilter swapFilter = new SwapFilter();
            assertThrows(UsernameNotFoundException.class, () -> testBookSwapsService.getSwaps(swapFilter));
        }

        //    @Test
        //    void confirmBookDelivery() {
        //    }

    }

    @Test
    void testGeUserAddressByUsername() {
        User user2 = new User();
        String username = "user2";
        user2.setUsername(username);
        Mockito.when(testUserRepository.findByUsername(username)).thenReturn(java.util.Optional.of(user2));
        testBookSwapsService.geUserAddressByUsername(username);
        verify(testUserRepository).findByUsername(username);
    }

    @Test
    void testGetUserAddressByUsernameUserNotFound() {
        String username = "user2";
        Mockito.when(testUserRepository.findByUsername(username)).thenReturn(java.util.Optional.empty());
        assertThrows(ApiNotFoundException.class, () -> testBookSwapsService.geUserAddressByUsername(username));
    }

}