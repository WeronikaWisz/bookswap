package com.bookswap.bookswapapp.services;

import com.bookswap.bookswapapp.dtos.bookswaps.SwapFilter;
import com.bookswap.bookswapapp.dtos.bookswaps.SwapListItem;
import com.bookswap.bookswapapp.enums.EBookLabel;
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
    private User user2;
    private SwapFilter swapFilter;
    private Swap swap;
    private Book book1;
    private Book book2;

    @BeforeEach
    void setUp() {
        testBookSwapsService = new BookSwapsService(testUserRepository, testSwapRepository);
        user = new User();
        user.setUsername("username");
        user.setPassword("password");
        user.setEmail("test1@gmail.com");
        testUserRepository.save(user);
        user2 = new User();
        user2.setUsername("username2");
        user2.setPassword("password");
        user2.setEmail("test2@gmail.com");
        testUserRepository.save(user2);
        swapFilter = new SwapFilter();
        swapFilter.setBookLabel(EBookLabel.TEMPORARY_SWAP);
        book1 = new Book();
        book1.setLabel(EBookLabel.TEMPORARY_SWAP);
        book1.setUser(user);
        testBookRepository.save(book1);
        book2 = new Book();
        book2.setLabel(EBookLabel.TEMPORARY_SWAP);
        book2.setUser(user2);
        testBookRepository.save(book2);
        swap = new Swap();
        swap.setBook1(book1);
        swap.setBook2(book2);
        testSwapRepository.save(swap);
    }

    @Nested
    class TestWithUser{

        @BeforeEach
        public void init() {
            UserDetailsI applicationUser = UserDetailsI.build(user);
            Authentication authentication = Mockito.mock(Authentication.class);
            SecurityContext securityContext = Mockito.mock(SecurityContext.class);
            Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
            SecurityContextHolder.setContext(securityContext);
            Mockito.when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(applicationUser);
        }

        @Test
        void testGetSwaps() {
            ESwapStatus status = ESwapStatus.IN_PROGRESS;

            List<ESwapStatus> statuses = new ArrayList<>();
            statuses.add(status);
            swapFilter.setSwapStatus(statuses);

            swap.setStatus(status);

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
            ESwapStatus status = ESwapStatus.COMPLETED;
            List<ESwapStatus> statuses = new ArrayList<>();
            statuses.add(status);
            swapFilter.setSwapStatus(statuses);

            book1.setUser(user2);
            book2.setUser(user);

            swap.setStatus(status);

            Mockito.when(testUserRepository.findByUsername("username")).thenReturn(java.util.Optional.of(user));
            Mockito.when(testSwapRepository.findUserSwaps(swapFilter.getSwapStatus(), swapFilter.getBookLabel(), user))
                    .thenReturn(Optional.of(List.of(swap)));

            List<SwapListItem> swaps = testBookSwapsService.getSwaps(swapFilter);

            verify(testSwapRepository).findUserSwaps(swapFilter.getSwapStatus(), swapFilter.getBookLabel(), user);
            assertEquals(1, swaps.size());
            assertEquals(swap.getId(), swaps.get(0).getId());
        }

        @Test
        void testGetSwapsStatusConfirmed() {
            ESwapStatus status = ESwapStatus.BOTH_CONFIRMED;
            List<ESwapStatus> statuses = new ArrayList<>();
            statuses.add(status);
            swapFilter.setSwapStatus(statuses);

            swap.setStatus(status);

            Mockito.when(testUserRepository.findByUsername("username")).thenReturn(java.util.Optional.of(user));
            Mockito.when(testSwapRepository.findUserSwaps(swapFilter.getSwapStatus(), swapFilter.getBookLabel(), user))
                    .thenReturn(Optional.of(List.of(swap)));

            List<SwapListItem> swaps = testBookSwapsService.getSwaps(swapFilter);

            verify(testSwapRepository).findUserSwaps(swapFilter.getSwapStatus(), swapFilter.getBookLabel(), user);
            assertEquals(1, swaps.size());
            assertEquals(swap.getId(), swaps.get(0).getId());
        }

        @Test
        void testGetSwapsStatusBook1ConfirmedOtherUserBook1() {
            ESwapStatus status = ESwapStatus.BOOK_1_CONFIRMED;
            List<ESwapStatus> statuses = new ArrayList<>();
            statuses.add(status);
            swapFilter.setSwapStatus(statuses);

            swap.setStatus(status);

            Mockito.when(testUserRepository.findByUsername("username")).thenReturn(java.util.Optional.of(user));
            Mockito.when(testSwapRepository.findUserSwaps(swapFilter.getSwapStatus(), swapFilter.getBookLabel(), user))
                    .thenReturn(Optional.of(List.of(swap)));

            List<SwapListItem> swaps = testBookSwapsService.getSwaps(swapFilter);

            verify(testSwapRepository).findUserSwaps(swapFilter.getSwapStatus(), swapFilter.getBookLabel(), user);
            assertEquals(1, swaps.size());
            assertEquals(swap.getId(), swaps.get(0).getId());
        }

        @Test
        void testGetSwapsStatusBook2ConfirmedOtherUserBook1() {
            ESwapStatus status = ESwapStatus.BOOK_2_CONFIRMED;
            List<ESwapStatus> statuses = new ArrayList<>();
            statuses.add(status);
            swapFilter.setSwapStatus(statuses);

            swap.setStatus(status);

            Mockito.when(testUserRepository.findByUsername("username")).thenReturn(java.util.Optional.of(user));
            Mockito.when(testSwapRepository.findUserSwaps(swapFilter.getSwapStatus(), swapFilter.getBookLabel(), user))
                    .thenReturn(Optional.of(List.of(swap)));

            List<SwapListItem> swaps = testBookSwapsService.getSwaps(swapFilter);

            verify(testSwapRepository).findUserSwaps(swapFilter.getSwapStatus(), swapFilter.getBookLabel(), user);
            assertEquals(1, swaps.size());
            assertEquals(swap.getId(), swaps.get(0).getId());
        }

        @Test
        void testGetSwapsStatusBook1Confirmed() {
            ESwapStatus status = ESwapStatus.BOOK_1_CONFIRMED;
            List<ESwapStatus> statuses = new ArrayList<>();
            statuses.add(status);
            swapFilter.setSwapStatus(statuses);

            book1.setUser(user2);
            book2.setUser(user);

            swap.setStatus(status);

            Mockito.when(testUserRepository.findByUsername("username")).thenReturn(java.util.Optional.of(user));
            Mockito.when(testSwapRepository.findUserSwaps(swapFilter.getSwapStatus(), swapFilter.getBookLabel(), user))
                    .thenReturn(Optional.of(List.of(swap)));

            List<SwapListItem> swaps = testBookSwapsService.getSwaps(swapFilter);

            verify(testSwapRepository).findUserSwaps(swapFilter.getSwapStatus(), swapFilter.getBookLabel(), user);
            assertEquals(1, swaps.size());
            assertEquals(swap.getId(), swaps.get(0).getId());
        }

        @Test
        void testGetSwapsStatusBook2Confirmed() {
            ESwapStatus status = ESwapStatus.BOOK_2_CONFIRMED;
            List<ESwapStatus> statuses = new ArrayList<>();
            statuses.add(status);
            swapFilter.setSwapStatus(statuses);

            book1.setUser(user2);
            book2.setUser(user);

            swap.setStatus(status);

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

        @Test
        void testConfirmBookDelivery() {
            Long id = swap.getId();
            swap.setStatus(ESwapStatus.IN_PROGRESS);
            Mockito.when(testUserRepository.findByUsername("username")).thenReturn(java.util.Optional.of(user));
            Mockito.when(testSwapRepository.findById(id)).thenReturn(Optional.of(swap));
            testBookSwapsService.confirmBookDelivery(id);
            verify(testSwapRepository).findById(id);
        }

        @Test
        void testConfirmBookDeliveryOtherUserBook1() {
            Long id = swap.getId();
            book1.setUser(user2);
            book2.setUser(user);
            swap.setStatus(ESwapStatus.IN_PROGRESS);
            Mockito.when(testUserRepository.findByUsername("username")).thenReturn(java.util.Optional.of(user));
            Mockito.when(testSwapRepository.findById(id)).thenReturn(Optional.of(swap));
            testBookSwapsService.confirmBookDelivery(id);
            verify(testSwapRepository).findById(id);
        }

        @Test
        void testConfirmBookDeliveryCompleted() {
            Long id = swap.getId();
            swap.setStatus(ESwapStatus.COMPLETED);
            Mockito.when(testUserRepository.findByUsername("username")).thenReturn(java.util.Optional.of(user));
            Mockito.when(testSwapRepository.findById(id)).thenReturn(Optional.of(swap));
            testBookSwapsService.confirmBookDelivery(id);
            verify(testSwapRepository).findById(id);
        }

        @Test
        void testConfirmBookDeliveryCompletedOtherUserBook1() {
            Long id = swap.getId();
            book1.setUser(user2);
            book2.setUser(user);
            swap.setStatus(ESwapStatus.COMPLETED);
            Mockito.when(testUserRepository.findByUsername("username")).thenReturn(java.util.Optional.of(user));
            Mockito.when(testSwapRepository.findById(id)).thenReturn(Optional.of(swap));
            testBookSwapsService.confirmBookDelivery(id);
            verify(testSwapRepository).findById(id);
        }

        @Test
        void testConfirmBookDeliveryBook1Confirmed() {
            Long id = swap.getId();
            swap.setStatus(ESwapStatus.BOOK_1_CONFIRMED);
            Mockito.when(testUserRepository.findByUsername("username")).thenReturn(java.util.Optional.of(user));
            Mockito.when(testSwapRepository.findById(id)).thenReturn(Optional.of(swap));
            testBookSwapsService.confirmBookDelivery(id);
            verify(testSwapRepository).findById(id);
        }

        @Test
        void testConfirmBookDeliveryBook2ConfirmedOtherUserBook1() {
            Long id = swap.getId();
            book1.setUser(user2);
            book2.setUser(user);
            swap.setStatus(ESwapStatus.BOOK_2_CONFIRMED);
            Mockito.when(testUserRepository.findByUsername("username")).thenReturn(java.util.Optional.of(user));
            Mockito.when(testSwapRepository.findById(id)).thenReturn(Optional.of(swap));
            testBookSwapsService.confirmBookDelivery(id);
            verify(testSwapRepository).findById(id);
        }

        @Test
        void testConfirmBookDeliveryBook1ConfirmedPermanent() {
            Long id = swap.getId();
            book1.setLabel(EBookLabel.PERMANENT_SWAP);
            book2.setLabel(EBookLabel.PERMANENT_SWAP);
            swap.setStatus(ESwapStatus.BOOK_1_CONFIRMED);
            Mockito.when(testUserRepository.findByUsername("username")).thenReturn(java.util.Optional.of(user));
            Mockito.when(testSwapRepository.findById(id)).thenReturn(Optional.of(swap));
            testBookSwapsService.confirmBookDelivery(id);
            verify(testSwapRepository).findById(id);
        }

        @Test
        void testConfirmBookDeliveryBook2ConfirmedOtherUserBook1Permanent() {
            Long id = swap.getId();
            book1.setUser(user2);
            book2.setUser(user);
            book1.setLabel(EBookLabel.PERMANENT_SWAP);
            book2.setLabel(EBookLabel.PERMANENT_SWAP);
            swap.setStatus(ESwapStatus.BOOK_2_CONFIRMED);
            Mockito.when(testUserRepository.findByUsername("username")).thenReturn(java.util.Optional.of(user));
            Mockito.when(testSwapRepository.findById(id)).thenReturn(Optional.of(swap));
            testBookSwapsService.confirmBookDelivery(id);
            verify(testSwapRepository).findById(id);
        }

        @Test
        void testConfirmBookDeliveryBothConfirmed() {
            Long id = swap.getId();
            swap.setStatus(ESwapStatus.BOTH_CONFIRMED);
            Mockito.when(testUserRepository.findByUsername("username")).thenReturn(java.util.Optional.of(user));
            Mockito.when(testSwapRepository.findById(id)).thenReturn(Optional.of(swap));
            testBookSwapsService.confirmBookDelivery(id);
            verify(testSwapRepository).findById(id);
        }

        @Test
        void testConfirmBookDeliveryBothConfirmedOtherUserBook1() {
            Long id = swap.getId();
            book1.setUser(user2);
            book2.setUser(user);
            swap.setStatus(ESwapStatus.BOTH_CONFIRMED);
            Mockito.when(testUserRepository.findByUsername("username")).thenReturn(java.util.Optional.of(user));
            Mockito.when(testSwapRepository.findById(id)).thenReturn(Optional.of(swap));
            testBookSwapsService.confirmBookDelivery(id);
            verify(testSwapRepository).findById(id);
        }

        @Test
        void testConfirmBookDeliveryBook2Returned() {
            Long id = swap.getId();
            swap.setStatus(ESwapStatus.BOOK_2_RETURNED);
            Mockito.when(testUserRepository.findByUsername("username")).thenReturn(java.util.Optional.of(user));
            Mockito.when(testSwapRepository.findById(id)).thenReturn(Optional.of(swap));
            testBookSwapsService.confirmBookDelivery(id);
            verify(testSwapRepository).findById(id);
        }

        @Test
        void testConfirmBookDeliveryBook1ReturnedOtherUserBook1() {
            Long id = swap.getId();
            book1.setUser(user2);
            book2.setUser(user);
            swap.setStatus(ESwapStatus.BOOK_1_RETURNED);
            Mockito.when(testUserRepository.findByUsername("username")).thenReturn(java.util.Optional.of(user));
            Mockito.when(testSwapRepository.findById(id)).thenReturn(Optional.of(swap));
            testBookSwapsService.confirmBookDelivery(id);
            verify(testSwapRepository).findById(id);
        }

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

    @Test
    void testConfirmBookDeliverySwapNotFound() {
        Long id = swap.getId();
        swap.setStatus(ESwapStatus.IN_PROGRESS);
        Mockito.when(testSwapRepository.findById(id)).thenReturn(Optional.empty());
        assertThrows(ApiNotFoundException.class, () -> testBookSwapsService.confirmBookDelivery(id));
    }

}