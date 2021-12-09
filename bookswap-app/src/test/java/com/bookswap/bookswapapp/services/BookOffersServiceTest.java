package com.bookswap.bookswapapp.services;

import com.bookswap.bookswapapp.dtos.bookoffers.OfferFilter;
import com.bookswap.bookswapapp.dtos.bookoffers.OffersResponse;
import com.bookswap.bookswapapp.dtos.bookswaps.SwapFilter;
import com.bookswap.bookswapapp.enums.EBookLabel;
import com.bookswap.bookswapapp.enums.EBookStatus;
import com.bookswap.bookswapapp.models.Book;
import com.bookswap.bookswapapp.models.Category;
import com.bookswap.bookswapapp.models.Swap;
import com.bookswap.bookswapapp.models.User;
import com.bookswap.bookswapapp.repositories.*;
import com.bookswap.bookswapapp.security.userdetails.UserDetailsI;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

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
    @Mock
    private CategoryRepository testCategoryRepository;
    private BookOffersService testBookOffersService;
    private User user;
    private User user2;
    private Book bookOffer;
    private Long bookId = 1L;
    private EBookLabel label;
    private String author = "author";
    private String title = "title";
    private String publisher = "publisher";
    private Integer yearOfPublication = 2021;
    private Category category;
    private String categoryName = "category";

    @BeforeEach
    void setUp() {
        testBookOffersService = new BookOffersService(testBookRepository, testUserRepository,
                testSwapRequestRepository, testSwapRepository);
        user = new User();
        user.setUsername("username");
        user.setPassword("password");
        user.setEmail("test1@gmail.com");
        testUserRepository.save(user);
        user2 = new User();
        user2.setUsername("username2");
        user2.setPassword("password");
        user2.setEmail("test2@gmail.com");
        user2.setCity("City");
        user2.setPostalCode("12-345");
        user2.setPost("Post");
        testUserRepository.save(user2);
        label = EBookLabel.TEMPORARY_SWAP;
        category = new Category();
        category.setName(categoryName);
        testCategoryRepository.save(category);
        bookOffer = new Book();
        bookOffer.setId(bookId);
        bookOffer.setLabel(label);
        bookOffer.setUser(user2);
        bookOffer.setStatus(EBookStatus.AVAILABLE);
        bookOffer.setAuthor(author);
        bookOffer.setTitle(title);
        bookOffer.setPublisher(publisher);
        bookOffer.setYearOfPublication(yearOfPublication);
        bookOffer.addCategory(category);
        testBookRepository.save(bookOffer);
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

        OffersResponse offersResponse = testBookOffersService.filterOffers(offerFilter);

        verify(testBookRepository).findBookByStatusAndLabelAndUserIsNot(status, offerFilter.getLabel(), user);
        assertEquals(1, offersResponse.getOffersList().size());
    }

    @Test
    void testFilterOffersUserNotFound() {
        Mockito.when(testUserRepository.findByUsername("username")).thenReturn(java.util.Optional.empty());
        OfferFilter offerFilter = new OfferFilter();
        assertThrows(UsernameNotFoundException.class, () -> testBookOffersService.filterOffers(offerFilter));
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

        OffersResponse offersResponse = testBookOffersService.filterOffers(offerFilter);

        verify(testBookRepository).findBookByStatusAndLabelAndUserIsNot(status, offerFilter.getLabel(), user);
        assertTrue(offersResponse.getOffersList().isEmpty());
    }

    @Test
    void loadFilterHints() {
        EBookStatus status = EBookStatus.AVAILABLE;

        Mockito.when(testUserRepository.findByUsername("username")).thenReturn(java.util.Optional.of(user));
        Mockito.when(testBookRepository.findBookByStatusAndUserIsNot(status, user)).thenReturn(Optional.of(List.of(bookOffer)));

        testBookOffersService.loadFilterHints();
        verify(testBookRepository).findBookByStatusAndUserIsNot(status, user);
    }
//
//    @Test
//    void getOffer() {
//    }
//
//    @Test
//    void sendSwapRequest() {
//    }
//
//    @Test
//    void getSentRequests() {
//    }
//
//    @Test
//    void getReceivedRequests() {
//    }
//
//    @Test
//    void cancelSwapRequest() {
//    }
//
//    @Test
//    void denySwapRequest() {
//    }
}