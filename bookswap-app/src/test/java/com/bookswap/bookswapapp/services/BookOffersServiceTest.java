package com.bookswap.bookswapapp.services;

import com.bookswap.bookswapapp.dtos.bookoffers.BooksForSwap;
import com.bookswap.bookswapapp.dtos.bookoffers.OfferFilter;
import com.bookswap.bookswapapp.dtos.bookoffers.OffersResponse;
import com.bookswap.bookswapapp.dtos.bookoffers.SwapRequestFilter;
import com.bookswap.bookswapapp.enums.EBookLabel;
import com.bookswap.bookswapapp.enums.EBookStatus;
import com.bookswap.bookswapapp.enums.ERequestStatus;
import com.bookswap.bookswapapp.exception.ApiExpectationFailedException;
import com.bookswap.bookswapapp.exception.ApiForbiddenException;
import com.bookswap.bookswapapp.exception.ApiNotFoundException;
import com.bookswap.bookswapapp.helpers.ImageHelper;
import com.bookswap.bookswapapp.models.*;
import com.bookswap.bookswapapp.repositories.*;
import com.bookswap.bookswapapp.security.userdetails.UserDetailsI;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class BookOffersServiceTest {

    @Mock
    private UserRepository testUserRepository;
    @Mock
    private SwapRepository testSwapRepository;
    @Mock
    private BookRepository testBookRepository;
    @Mock
    private SwapRequestRepository testSwapRequestRepository;
    private BookOffersService testBookOffersService;
    private User user;
    private Book bookOffer;
    private final Long bookId = 1L;
    private EBookLabel label;
    private final String author = "author";
    private final String title = "title";
    private final String publisher = "publisher";
    private final String categoryName = "category";
    private SwapRequest swapRequest;
    private Book bookOther;
    private final Long bookOtherId = 2L;
    private final Long swapRequestId = 2L;

    @BeforeEach
    void setUp() throws IOException {
        testBookOffersService = new BookOffersService(testBookRepository, testUserRepository,
                testSwapRequestRepository, testSwapRepository);
        user = new User();
        user.setUsername("username");
        user.setPassword("password");
        user.setEmail("test1@gmail.com");
        User user2 = new User();
        user2.setUsername("username2");
        user2.setPassword("password");
        user2.setEmail("test2@gmail.com");
        user2.setCity("City");
        user2.setPostalCode("12-345");
        user2.setPost("Post");
        label = EBookLabel.TEMPORARY_SWAP;
        Category category = new Category();
        category.setName(categoryName);
        MockMultipartFile image = new MockMultipartFile("image", new byte[1]);
        bookOffer = new Book();
        bookOffer.setId(bookId);
        bookOffer.setLabel(label);
        bookOffer.setUser(user2);
        bookOffer.setStatus(EBookStatus.AVAILABLE);
        bookOffer.setAuthor(author);
        bookOffer.setTitle(title);
        bookOffer.setPublisher(publisher);
        Integer yearOfPublication = 2021;
        bookOffer.setYearOfPublication(yearOfPublication);
        bookOffer.addCategory(category);
        bookOffer.setImage(ImageHelper.compressBytes(image.getBytes()));
        bookOther = new Book();
        bookOther.setId(bookOtherId);
        bookOther.setUser(user);
        bookOther.setStatus(EBookStatus.AVAILABLE);
        bookOther.setTitle(title);
        bookOther.setAuthor(author);
        bookOther.setLabel(label);
        bookOther.setImage(ImageHelper.compressBytes(image.getBytes()));
        swapRequest = new SwapRequest();
        swapRequest.setId(swapRequestId);
        swapRequest.setUser(user2);
        swapRequest.setStatus(ERequestStatus.WAITING);
        swapRequest.setBook(bookOther);
    }

    @Nested
    class TestWithUser {

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
        void testFilterOffers() {
            OfferFilter offerFilter = new OfferFilter();
            offerFilter.setLabel(label);
            offerFilter.setLocalization(List.of("City, 12-345 Post"));
            offerFilter.setAuthors(List.of(author));
            offerFilter.setTitles(List.of(title));
            offerFilter.setOwners(List.of("username2"));
            offerFilter.setPublishers(List.of(publisher));
            offerFilter.setYearOfPublicationFrom("1999");
            offerFilter.setYearOfPublicationTo("2021");
            offerFilter.setCategories(List.of(categoryName));

            EBookStatus status = EBookStatus.AVAILABLE;

            Mockito.when(testUserRepository.findByUsername("username")).thenReturn(java.util.Optional.of(user));
            Mockito.when(testBookRepository.findBookByStatusAndLabelAndUserIsNot(status, offerFilter.getLabel(), user))
                    .thenReturn(Optional.of(List.of(bookOffer)));

            OffersResponse offersResponse = testBookOffersService.filterOffers(offerFilter, 0, 10);

            verify(testBookRepository).findBookByStatusAndLabelAndUserIsNot(status, offerFilter.getLabel(), user);
            assertEquals(1, offersResponse.getOffersList().size());
        }

        @Test
        void testFilterOffersUserNotFound() {
            Mockito.when(testUserRepository.findByUsername("username")).thenReturn(java.util.Optional.empty());
            OfferFilter offerFilter = new OfferFilter();
            assertThrows(UsernameNotFoundException.class, () -> testBookOffersService.filterOffers(offerFilter, 0, 10));
        }

        @Test
        void testFilterOffersASendRequestBefore() {
            OfferFilter offerFilter = new OfferFilter();
            offerFilter.setLabel(label);

            EBookStatus status = EBookStatus.AVAILABLE;

            Mockito.when(testUserRepository.findByUsername("username")).thenReturn(java.util.Optional.of(user));
            Mockito.when(testBookRepository.findBookByStatusAndLabelAndUserIsNot(status, offerFilter.getLabel(), user))
                    .thenReturn(Optional.of(List.of(bookOffer)));
            Mockito.when(testSwapRequestRepository.userSendRequestsBooks(label, user))
                    .thenReturn(Optional.of(List.of(bookId)));

            OffersResponse offersResponse = testBookOffersService.filterOffers(offerFilter, 0, 10);

            verify(testBookRepository).findBookByStatusAndLabelAndUserIsNot(status, offerFilter.getLabel(), user);
            assertTrue(offersResponse.getOffersList().isEmpty());
        }

        @Test
        void testLoadFilterHints() {
            EBookStatus status = EBookStatus.AVAILABLE;

            Mockito.when(testUserRepository.findByUsername("username")).thenReturn(java.util.Optional.of(user));
            Mockito.when(testBookRepository.findBookByStatusAndUserIsNot(status, user)).thenReturn(Optional.of(List.of(bookOffer)));

            testBookOffersService.loadFilterHints();
            verify(testBookRepository).findBookByStatusAndUserIsNot(status, user);
        }

        @Test
        void testGetOffer() {
            Mockito.when(testUserRepository.findByUsername("username")).thenReturn(java.util.Optional.of(user));
            Mockito.when(testBookRepository.findById(bookId)).thenReturn(Optional.of(bookOffer));
            Mockito.when(testSwapRequestRepository.findOffersSendFromUser(bookOffer.getUser(), user, bookOffer.getLabel()))
                    .thenReturn(Optional.empty());

            testBookOffersService.getOffer(bookId);

            verify(testBookRepository).findById(bookId);
            verify(testSwapRequestRepository).findOffersSendFromUser(bookOffer.getUser(), user, bookOffer.getLabel());

        }

        @Test
        void testGetOfferHasOfferFromOtherUser() {

            Mockito.when(testUserRepository.findByUsername("username")).thenReturn(java.util.Optional.of(user));
            Mockito.when(testBookRepository.findById(bookId)).thenReturn(Optional.of(bookOffer));
            Mockito.when(testSwapRequestRepository.findOffersSendFromUser(bookOffer.getUser(), user, bookOffer.getLabel()))
                    .thenReturn(Optional.of(List.of(swapRequest)));

            testBookOffersService.getOffer(bookId);

            verify(testBookRepository).findById(bookId);
            verify(testSwapRequestRepository).findOffersSendFromUser(bookOffer.getUser(), user, bookOffer.getLabel());

        }

        @Test
        void testGetSentRequests() {
            SwapRequest swapRequest2 = new SwapRequest();
            swapRequest2.setUser(user);
            swapRequest2.setStatus(ERequestStatus.WAITING);
            swapRequest2.setBook(bookOffer);
            testSwapRequestRepository.save(swapRequest2);

            ERequestStatus status = ERequestStatus.WAITING;
            List<ERequestStatus> statuses = new ArrayList<>();
            statuses.add(status);

            SwapRequestFilter swapRequestFilter = new SwapRequestFilter();
            swapRequestFilter.setBookLabel(label);
            swapRequestFilter.setRequestStatus(statuses);

            Mockito.when(testUserRepository.findByUsername("username")).thenReturn(java.util.Optional.of(user));
            Mockito.when(testSwapRequestRepository.findByUserAndStatusIn(user, swapRequestFilter.getRequestStatus()))
                    .thenReturn(Optional.of(List.of(swapRequest2)));

            testBookOffersService.getSentRequests(swapRequestFilter, 0, 10);

            verify(testSwapRequestRepository).findByUserAndStatusIn(user, swapRequestFilter.getRequestStatus());
        }

        @Test
        void testGetReceivedRequests() {
            ERequestStatus status = ERequestStatus.WAITING;
            List<ERequestStatus> statuses = new ArrayList<>();
            statuses.add(status);

            SwapRequestFilter swapRequestFilter = new SwapRequestFilter();
            swapRequestFilter.setBookLabel(label);
            swapRequestFilter.setRequestStatus(statuses);

            Mockito.when(testUserRepository.findByUsername("username")).thenReturn(java.util.Optional.of(user));
            Mockito.when(testSwapRequestRepository.findByBook_UserAndStatusIn(user, swapRequestFilter.getRequestStatus()))
                    .thenReturn(Optional.of(List.of(swapRequest)));

            testBookOffersService.getReceivedRequests(swapRequestFilter, 0, 10);

            verify(testSwapRequestRepository).findByBook_UserAndStatusIn(user, swapRequestFilter.getRequestStatus());
        }

        @Test
        void testCancelSwapRequest() {
            Long id = 2L;
            SwapRequest swapRequest2 = new SwapRequest();
            swapRequest2.setId(id);
            swapRequest2.setUser(user);
            swapRequest2.setStatus(ERequestStatus.WAITING);
            swapRequest2.setBook(bookOffer);

            Mockito.when(testUserRepository.findByUsername("username")).thenReturn(java.util.Optional.of(user));
            Mockito.when(testSwapRequestRepository.findById(id)).thenReturn(Optional.of(swapRequest2));

            testBookOffersService.cancelSwapRequest(id);

            assertEquals(swapRequest2.getStatus(), ERequestStatus.CANCELED);
        }

        @Test
        void testCancelOtherUserSwapRequest() {

            Mockito.when(testUserRepository.findByUsername("username")).thenReturn(java.util.Optional.of(user));
            Mockito.when(testSwapRequestRepository.findById(swapRequestId)).thenReturn(Optional.of(swapRequest));
            assertThrows(ApiForbiddenException.class, () -> testBookOffersService.cancelSwapRequest(swapRequestId));

        }

        @Test
        void testDenySwapRequest() {

            Mockito.when(testUserRepository.findByUsername("username")).thenReturn(java.util.Optional.of(user));
            Mockito.when(testSwapRequestRepository.findById(swapRequestId)).thenReturn(Optional.of(swapRequest));

            testBookOffersService.denySwapRequest(swapRequestId);

            assertEquals(swapRequest.getStatus(), ERequestStatus.DENIED);
        }

        @Test
        void testDenyOtherUserSwapRequest() {
            Long id = 2L;
            SwapRequest swapRequest2 = new SwapRequest();
            swapRequest2.setId(id);
            swapRequest2.setUser(user);
            swapRequest2.setStatus(ERequestStatus.WAITING);
            swapRequest2.setBook(bookOffer);

            Mockito.when(testUserRepository.findByUsername("username")).thenReturn(java.util.Optional.of(user));
            Mockito.when(testSwapRequestRepository.findById(id)).thenReturn(Optional.of(swapRequest2));

            assertThrows(ApiForbiddenException.class, () -> testBookOffersService.denySwapRequest(id));
        }

        @Test
        void testSendSwapRequestNoAvailableBooksForSwap() {
            BooksForSwap booksForSwap = new BooksForSwap();
            booksForSwap.setRequestedBookId(bookId);

            Mockito.when(testBookRepository.findById(booksForSwap.getRequestedBookId())).thenReturn(Optional.of(bookOffer));
            Mockito.when(testUserRepository.findByUsername("username")).thenReturn(java.util.Optional.of(user));
            Mockito.when(testSwapRequestRepository.countUserOfferRequest(bookOffer.getLabel(), user)).thenReturn(1L);
            Mockito.when(testBookRepository.countUserAvailableBooks(bookOffer.getLabel(), user)).thenReturn(1L);

            assertThrows(ApiExpectationFailedException.class, () -> testBookOffersService.sendSwapRequest(booksForSwap));
        }

        @Test
        void testSendSwapRequestAlreadyExists() {
            BooksForSwap booksForSwap = new BooksForSwap();
            booksForSwap.setRequestedBookId(bookId);

            Mockito.when(testBookRepository.findById(booksForSwap.getRequestedBookId())).thenReturn(Optional.of(bookOffer));
            Mockito.when(testUserRepository.findByUsername("username")).thenReturn(java.util.Optional.of(user));
            Mockito.when(testSwapRequestRepository.countUserOfferRequest(bookOffer.getLabel(), user)).thenReturn(1L);
            Mockito.when(testBookRepository.countUserAvailableBooks(bookOffer.getLabel(), user)).thenReturn(2L);
            Mockito.when(testSwapRequestRepository.findByBookAndUserAndStatus(bookOffer, user, ERequestStatus.WAITING))
                    .thenReturn(Optional.of(new SwapRequest()));

            assertThrows(ApiExpectationFailedException.class, () -> testBookOffersService.sendSwapRequest(booksForSwap));

        }

        @Test
        void testSendSwapRequest() {
            BooksForSwap booksForSwap = new BooksForSwap();
            booksForSwap.setRequestedBookId(bookId);

            Mockito.when(testBookRepository.findById(booksForSwap.getRequestedBookId())).thenReturn(Optional.of(bookOffer));
            Mockito.when(testUserRepository.findByUsername("username")).thenReturn(java.util.Optional.of(user));
            Mockito.when(testSwapRequestRepository.countUserOfferRequest(bookOffer.getLabel(), user)).thenReturn(1L);
            Mockito.when(testBookRepository.countUserAvailableBooks(bookOffer.getLabel(), user)).thenReturn(2L);
            Mockito.when(testSwapRequestRepository.findByBookAndUserAndStatus(bookOffer, user, ERequestStatus.WAITING))
                    .thenReturn(Optional.empty());

            testBookOffersService.sendSwapRequest(booksForSwap);

            verify(testSwapRequestRepository).save(any(SwapRequest.class));
        }

        @Test
        void testSendSwapNotFound() {
            BooksForSwap booksForSwap = new BooksForSwap();
            booksForSwap.setRequestedBookId(bookId);
            booksForSwap.setUserBookIdForSwap(bookOtherId);

            Mockito.when(testBookRepository.findById(booksForSwap.getRequestedBookId())).thenReturn(Optional.of(bookOffer));
            Mockito.when(testUserRepository.findByUsername("username")).thenReturn(java.util.Optional.of(user));
            Mockito.when(testSwapRequestRepository.countUserOfferRequest(bookOffer.getLabel(), user)).thenReturn(1L);
            Mockito.when(testBookRepository.countUserAvailableBooks(bookOffer.getLabel(), user)).thenReturn(2L);
            Mockito.when(testBookRepository.findById(booksForSwap.getUserBookIdForSwap())).thenReturn(Optional.empty());

            assertThrows(ApiNotFoundException.class, () -> testBookOffersService.sendSwapRequest(booksForSwap));
        }

        @Test
        void testSendSwapNotAvailable() {
            BooksForSwap booksForSwap = new BooksForSwap();
            booksForSwap.setRequestedBookId(bookId);
            booksForSwap.setUserBookIdForSwap(bookOtherId);

            bookOther.setStatus(EBookStatus.TEMPORARY_SWAP);

            Mockito.when(testBookRepository.findById(booksForSwap.getRequestedBookId())).thenReturn(Optional.of(bookOffer));
            Mockito.when(testUserRepository.findByUsername("username")).thenReturn(java.util.Optional.of(user));
            Mockito.when(testSwapRequestRepository.countUserOfferRequest(bookOffer.getLabel(), user)).thenReturn(1L);
            Mockito.when(testBookRepository.countUserAvailableBooks(bookOffer.getLabel(), user)).thenReturn(2L);
            Mockito.when(testBookRepository.findById(booksForSwap.getUserBookIdForSwap())).thenReturn(Optional.of(bookOther));

            assertThrows(ApiExpectationFailedException.class, () -> testBookOffersService.sendSwapRequest(booksForSwap));
        }

        @Test
        void testSendSwapLabelNotMatch() {
            BooksForSwap booksForSwap = new BooksForSwap();
            booksForSwap.setRequestedBookId(bookId);
            booksForSwap.setUserBookIdForSwap(bookOtherId);

            bookOther.setLabel(EBookLabel.PERMANENT_SWAP);

            Mockito.when(testBookRepository.findById(booksForSwap.getRequestedBookId())).thenReturn(Optional.of(bookOffer));
            Mockito.when(testUserRepository.findByUsername("username")).thenReturn(java.util.Optional.of(user));
            Mockito.when(testSwapRequestRepository.countUserOfferRequest(bookOffer.getLabel(), user)).thenReturn(1L);
            Mockito.when(testBookRepository.countUserAvailableBooks(bookOffer.getLabel(), user)).thenReturn(2L);
            Mockito.when(testBookRepository.findById(booksForSwap.getUserBookIdForSwap())).thenReturn(Optional.of(bookOther));

            assertThrows(ApiExpectationFailedException.class, () -> testBookOffersService.sendSwapRequest(booksForSwap));
        }

        @Test
        void testSendSwapRequestNotWaiting() {
            BooksForSwap booksForSwap = new BooksForSwap();
            booksForSwap.setRequestedBookId(bookId);
            booksForSwap.setUserBookIdForSwap(bookOtherId);

            Mockito.when(testBookRepository.findById(booksForSwap.getRequestedBookId())).thenReturn(Optional.of(bookOffer));
            Mockito.when(testUserRepository.findByUsername("username")).thenReturn(java.util.Optional.of(user));
            Mockito.when(testSwapRequestRepository.countUserOfferRequest(bookOffer.getLabel(), user)).thenReturn(1L);
            Mockito.when(testBookRepository.countUserAvailableBooks(bookOffer.getLabel(), user)).thenReturn(2L);
            Mockito.when(testBookRepository.findById(booksForSwap.getUserBookIdForSwap())).thenReturn(Optional.of(bookOther));
            Mockito.when(testSwapRequestRepository.findByBookAndUserAndStatus(bookOther,
                    bookOffer.getUser(), ERequestStatus.WAITING)).thenReturn(Optional.empty());

            assertThrows(ApiExpectationFailedException.class, () -> testBookOffersService.sendSwapRequest(booksForSwap));
        }

        @Test
        void testSendSwap() {
            BooksForSwap booksForSwap = new BooksForSwap();
            booksForSwap.setRequestedBookId(bookId);
            booksForSwap.setUserBookIdForSwap(bookOtherId);

            Mockito.when(testBookRepository.findById(booksForSwap.getRequestedBookId())).thenReturn(Optional.of(bookOffer));
            Mockito.when(testUserRepository.findByUsername("username")).thenReturn(java.util.Optional.of(user));
            Mockito.when(testSwapRequestRepository.countUserOfferRequest(bookOffer.getLabel(), user)).thenReturn(1L);
            Mockito.when(testBookRepository.countUserAvailableBooks(bookOffer.getLabel(), user)).thenReturn(2L);
            Mockito.when(testBookRepository.findById(booksForSwap.getUserBookIdForSwap())).thenReturn(Optional.of(bookOther));
            Mockito.when(testSwapRequestRepository.findByBookAndUserAndStatus(bookOther,
                    bookOffer.getUser(), ERequestStatus.WAITING)).thenReturn(Optional.of(swapRequest));

            testBookOffersService.sendSwapRequest(booksForSwap);

            verify(testSwapRepository).save(any(Swap.class));
        }

        @Test
        void testSendSwapPermanent() {
            BooksForSwap booksForSwap = new BooksForSwap();
            booksForSwap.setRequestedBookId(bookId);
            booksForSwap.setUserBookIdForSwap(bookOtherId);

            bookOffer.setLabel(EBookLabel.PERMANENT_SWAP);
            bookOther.setLabel(EBookLabel.PERMANENT_SWAP);

            Mockito.when(testBookRepository.findById(booksForSwap.getRequestedBookId())).thenReturn(Optional.of(bookOffer));
            Mockito.when(testUserRepository.findByUsername("username")).thenReturn(java.util.Optional.of(user));
            Mockito.when(testSwapRequestRepository.countUserOfferRequest(bookOffer.getLabel(), user)).thenReturn(1L);
            Mockito.when(testBookRepository.countUserAvailableBooks(bookOffer.getLabel(), user)).thenReturn(2L);
            Mockito.when(testBookRepository.findById(booksForSwap.getUserBookIdForSwap())).thenReturn(Optional.of(bookOther));
            Mockito.when(testSwapRequestRepository.findByBookAndUserAndStatus(bookOther,
                    bookOffer.getUser(), ERequestStatus.WAITING)).thenReturn(Optional.of(swapRequest));
            Mockito.when(testSwapRequestRepository.findByBookInAndStatusAndBook_Label(List.of(bookOffer, bookOther),
                    ERequestStatus.WAITING, EBookLabel.PERMANENT_SWAP)).thenReturn(Optional.of(List.of(new SwapRequest())));

            testBookOffersService.sendSwapRequest(booksForSwap);

            verify(testSwapRepository).save(any(Swap.class));
        }

    }

    @Test
    void testGetOfferNotFound() {
        Mockito.when(testBookRepository.findById(bookId)).thenReturn(Optional.empty());
        assertThrows(ApiNotFoundException.class, () -> testBookOffersService.getOffer(bookId));
    }

    @Test
    void testDenySwapRequestNotFound() {
        Mockito.when(testSwapRequestRepository.findById(swapRequestId)).thenReturn(Optional.empty());
        assertThrows(ApiNotFoundException.class, () -> testBookOffersService.denySwapRequest(swapRequestId));
    }

    @Test
    void testDenySwapRequestNotWaiting() {
        swapRequest.setStatus(ERequestStatus.CANCELED);
        Mockito.when(testSwapRequestRepository.findById(swapRequestId)).thenReturn(Optional.of(swapRequest));
        assertThrows(ApiExpectationFailedException.class, () -> testBookOffersService.denySwapRequest(swapRequestId));
    }

    @Test
    void testSendSwapRequestRequestedBookNotFound() {
        BooksForSwap booksForSwap = new BooksForSwap();
        booksForSwap.setRequestedBookId(bookId);
        Mockito.when(testBookRepository.findById(booksForSwap.getRequestedBookId())).thenReturn(Optional.empty());
        assertThrows(ApiNotFoundException.class, () -> testBookOffersService.sendSwapRequest(booksForSwap));
    }

    @Test
    void testSendSwapRequestRequestedBookNotAvailable() {
        bookOffer.setStatus(EBookStatus.TEMPORARY_SWAP);
        BooksForSwap booksForSwap = new BooksForSwap();
        booksForSwap.setRequestedBookId(bookId);
        Mockito.when(testBookRepository.findById(booksForSwap.getRequestedBookId())).thenReturn(Optional.of(bookOffer));
        assertThrows(ApiExpectationFailedException.class, () -> testBookOffersService.sendSwapRequest(booksForSwap));
    }

}