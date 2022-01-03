package com.bookswap.bookswapapp.services;

import com.bookswap.bookswapapp.dtos.userbooks.BookData;
import com.bookswap.bookswapapp.dtos.userbooks.BookFilter;
import com.bookswap.bookswapapp.dtos.userbooks.BooksResponse;
import com.bookswap.bookswapapp.enums.EBookLabel;
import com.bookswap.bookswapapp.enums.EBookStatus;
import com.bookswap.bookswapapp.exception.ApiExpectationFailedException;
import com.bookswap.bookswapapp.exception.ApiForbiddenException;
import com.bookswap.bookswapapp.exception.ApiNotFoundException;
import com.bookswap.bookswapapp.helpers.ImageHelper;
import com.bookswap.bookswapapp.models.Book;
import com.bookswap.bookswapapp.models.Category;
import com.bookswap.bookswapapp.models.User;
import com.bookswap.bookswapapp.repositories.BookRepository;
import com.bookswap.bookswapapp.repositories.CategoryRepository;
import com.bookswap.bookswapapp.repositories.UserRepository;
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
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserBooksServiceTest {

    @Mock
    private UserRepository testUserRepository;
    @Mock
    private BookRepository testBookRepository;
    @Mock
    private CategoryRepository testCategoryRepository;
    private UserBooksService testUserBooksService;
    private User user;
    private Book book;
    private final Long bookId = 1L;
    private EBookLabel label;
    private final String author = "author";
    private final String title = "title";
    private final String publisher = "publisher";
    private Category category;
    private final String categoryName = "category";

    @BeforeEach
    void setUp() {
        testUserBooksService = new UserBooksService(testBookRepository, testCategoryRepository, testUserRepository);
        user = new User();
        user.setUsername("username");
        user.setPassword("password");
        user.setEmail("test1@gmail.com");
        label = EBookLabel.TEMPORARY_SWAP;
        category = new Category();
        category.setName(categoryName);
        book = new Book();
        book.setId(bookId);
        book.setLabel(label);
        book.setUser(user);
        book.setStatus(EBookStatus.AVAILABLE);
        book.setAuthor(author);
        book.setTitle(title);
        book.setPublisher(publisher);
        Integer yearOfPublication = 2021;
        book.setYearOfPublication(yearOfPublication);
        book.addCategory(category);
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
        void testAddBook() {
            Mockito.when(testUserRepository.findByUsername("username")).thenReturn(java.util.Optional.of(user));
            Mockito.when(testCategoryRepository.findCategoryByName(categoryName)).thenReturn(Optional.of(category));

            testUserBooksService.addBook(book, List.of(categoryName), label);

            verify(testBookRepository).save(any(Book.class));
        }

        @Test
        void testAddBookNewCategory() {
            Mockito.when(testUserRepository.findByUsername("username")).thenReturn(java.util.Optional.of(user));

            testUserBooksService.addBook(book, List.of("newCategory"), label);

            verify(testBookRepository).save(any(Book.class));
            verify(testCategoryRepository).save(any(Category.class));
        }

        @Test
        void testAddBookImage() throws IOException {

            MockMultipartFile image = new MockMultipartFile("image", new byte[1]);

            Mockito.when(testUserRepository.findByUsername("username")).thenReturn(java.util.Optional.of(user));
            Mockito.when(testCategoryRepository.findCategoryByName(categoryName)).thenReturn(Optional.of(category));

            testUserBooksService.addBook(image, book, List.of(categoryName), label);

            verify(testBookRepository).save(any(Book.class));

        }

        @Test
        void testUpdateBookImage() throws IOException {

            MockMultipartFile image = new MockMultipartFile("image", new byte[1]);

            BookData bookData = new BookData();

            Mockito.when(testUserRepository.findByUsername("username")).thenReturn(java.util.Optional.of(user));
            Mockito.when(testBookRepository.findById(bookId)).thenReturn(Optional.of(book));

            testUserBooksService.updateBook(bookId, image, bookData, List.of("newCategory"), EBookLabel.PERMANENT_SWAP);

            assertNotNull(book.getImage());
        }

        @Test
        void testUpdateBook() {
            String newAuthor = "newAuthor";
            String newTitle = "newTitle";
            EBookLabel newLabel = EBookLabel.PERMANENT_SWAP;
            String newPublisher = "newPublisher";
            Integer newYearOfPublication = 2000;
            String newDescription = "newDescription";
            List<String> newCategories = List.of("newCategory");

            BookData bookData = new BookData();
            bookData.setAuthor(newAuthor);
            bookData.setTitle(newTitle);
            bookData.setLabel(newLabel);
            bookData.setPublisher(newPublisher);
            bookData.setYearOfPublication(newYearOfPublication);
            bookData.setDescription(newDescription);
            bookData.setCategories(newCategories);

            Mockito.when(testUserRepository.findByUsername("username")).thenReturn(java.util.Optional.of(user));
            Mockito.when(testBookRepository.findById(bookId)).thenReturn(Optional.of(book));

            testUserBooksService.updateBook(bookId, bookData, newCategories, newLabel);

            assertEquals(newAuthor, book.getAuthor());
            assertEquals(newTitle, book.getTitle());
            assertEquals(newPublisher, book.getPublisher());
            assertEquals(newYearOfPublication, book.getYearOfPublication());
            assertEquals(newDescription, book.getDescription());
            assertEquals(newLabel, book.getLabel());

        }

        @Test
        void testUpdateBookNotUserBook() {
            BookData bookData = new BookData();
            User otherUser = new User();
            otherUser.setUsername("username2");
            otherUser.setPassword("password");
            otherUser.setEmail("test2@gmail.com");
            book.setUser(otherUser);

            Mockito.when(testUserRepository.findByUsername("username")).thenReturn(java.util.Optional.of(user));
            Mockito.when(testBookRepository.findById(bookId)).thenReturn(Optional.of(book));

            assertThrows(ApiForbiddenException.class, () ->
                    testUserBooksService.updateBook(
                            bookId, bookData, List.of("newCategory"), EBookLabel.PERMANENT_SWAP));

        }

        @Test
        void testUpdateBookNotAvailable() {
            BookData bookData = new BookData();

            book.setStatus(EBookStatus.TEMPORARY_SWAP);

            Mockito.when(testUserRepository.findByUsername("username")).thenReturn(java.util.Optional.of(user));
            Mockito.when(testBookRepository.findById(bookId)).thenReturn(Optional.of(book));

            assertThrows(ApiExpectationFailedException.class, () ->
                    testUserBooksService.updateBook(
                            bookId, bookData, List.of("newCategoty"), EBookLabel.PERMANENT_SWAP));

        }

        @Test
        void testLoadFilterHints() {

            Mockito.when(testUserRepository.findByUsername("username")).thenReturn(java.util.Optional.of(user));
            Mockito.when(testBookRepository.findBookByUser(user)).thenReturn(Optional.of(List.of(book)));

            testUserBooksService.loadFilterHints();
            verify(testBookRepository).findBookByUser(user);
        }

        @Test
        void testLoadFilterHintsUserNotFound() {

            Mockito.when(testUserRepository.findByUsername("username")).thenReturn(java.util.Optional.empty());
            assertThrows(UsernameNotFoundException.class, () -> testUserBooksService.loadFilterHints());
        }

        @Test
        void testFilterBooks() throws IOException {
            MockMultipartFile image = new MockMultipartFile("image", new byte[1]);
            book.setImage(ImageHelper.compressBytes(image.getBytes()));

            BookFilter bookFilter = new BookFilter();
            bookFilter.setStatus(EBookStatus.AVAILABLE);
            bookFilter.setLabel(label);
            bookFilter.setAuthors(List.of(author));
            bookFilter.setTitles(List.of(title));
            bookFilter.setPublishers(List.of(publisher));
            bookFilter.setYearOfPublicationFrom("1999");
            bookFilter.setYearOfPublicationTo("2021");
            bookFilter.setCategories(List.of(categoryName));

            Mockito.when(testUserRepository.findByUsername("username")).thenReturn(java.util.Optional.of(user));
            Mockito.when(testBookRepository.findBookByStatusAndLabelAndUser(bookFilter.getStatus(),
                    bookFilter.getLabel(), user)).thenReturn(Optional.of(List.of(book)));

            BooksResponse booksResponse = testUserBooksService.filterBooks(bookFilter, 0, 10);

            verify(testBookRepository).findBookByStatusAndLabelAndUser(bookFilter.getStatus(),
                    bookFilter.getLabel(), user);
            assertEquals(1, booksResponse.getBooksList().size());

        }

        @Test
        void testFilterBooksWithoutLabel() {
            BookFilter bookFilter = new BookFilter();
            bookFilter.setLabel(label);

            Mockito.when(testUserRepository.findByUsername("username")).thenReturn(java.util.Optional.of(user));
            Mockito.when(testBookRepository.findBookByLabelAndUser(bookFilter.getLabel(),
                    user)).thenReturn(Optional.of(List.of(book)));

            BooksResponse booksResponse = testUserBooksService.filterBooks(bookFilter, 0, 10);

            verify(testBookRepository).findBookByLabelAndUser(bookFilter.getLabel(), user);
            assertEquals(1, booksResponse.getBooksList().size());

        }

        @Test
        void testFilterBooksWithoutStatus() {
            BookFilter bookFilter = new BookFilter();
            bookFilter.setStatus(EBookStatus.AVAILABLE);

            Mockito.when(testUserRepository.findByUsername("username")).thenReturn(java.util.Optional.of(user));
            Mockito.when(testBookRepository.findBookByStatusAndUser(bookFilter.getStatus(),
                    user)).thenReturn(Optional.of(List.of(book)));

            BooksResponse booksResponse = testUserBooksService.filterBooks(bookFilter, 0, 10);

            verify(testBookRepository).findBookByStatusAndUser(bookFilter.getStatus(), user);
            assertEquals(1, booksResponse.getBooksList().size());

        }

        @Test
        void testFilterBooksWithoutStatusAndLabel() {
            BookFilter bookFilter = new BookFilter();

            Mockito.when(testUserRepository.findByUsername("username")).thenReturn(java.util.Optional.of(user));
            Mockito.when(testBookRepository.findBookByUser(user)).thenReturn(Optional.of(List.of(book)));

            BooksResponse booksResponse = testUserBooksService.filterBooks(bookFilter, 0 ,10);

            verify(testBookRepository).findBookByUser(user);
            assertEquals(1, booksResponse.getBooksList().size());

        }

    }

    @Test
    void testGetBook() {
        Mockito.when(testBookRepository.findById(bookId)).thenReturn(Optional.of(book));
        testUserBooksService.getBook(bookId);
        verify(testBookRepository).findById(bookId);
    }

    @Test
    void testGetBookNotFound() {
        Mockito.when(testBookRepository.findById(bookId)).thenReturn(Optional.empty());
        assertThrows(ApiNotFoundException.class, () -> testUserBooksService.getBook(bookId));
    }

    @Test
    void testLoadCategoriesNames() {
        Mockito.when(testCategoryRepository.findAll()).thenReturn(List.of(category));
        testUserBooksService.loadCategoriesNames();
        verify(testCategoryRepository).findAll();
    }
}