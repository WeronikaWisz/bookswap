package com.bookswap.bookswapapp.controllers;

import com.bookswap.bookswapapp.dtos.userbooks.*;
import com.bookswap.bookswapapp.enums.EBookLabel;
import com.bookswap.bookswapapp.models.Book;
import com.bookswap.bookswapapp.security.jwt.AuthEntryPointJwt;
import com.bookswap.bookswapapp.security.jwt.JwtUtils;
import com.bookswap.bookswapapp.security.userdetails.UserDetailsServiceI;
import com.bookswap.bookswapapp.services.UserBooksService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.MessageSource;
import org.springframework.context.support.DelegatingMessageSource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserBooksController.class)
class UserBooksControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserBooksService testUserBooksService;

    @MockBean
    private ModelMapper modelMapper;

    private MessageSource messageSource;

    @Autowired
    private DelegatingMessageSource delegatingMessageSource;

    @MockBean
    private UserDetailsServiceI userDetailsServiceI;

    @MockBean
    private AuthEntryPointJwt authEntryPointJwt;

    @MockBean
    private JwtUtils jwtUtils;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(this.context)
                .apply(springSecurity())
                .build();
        messageSource = Mockito.mock(MessageSource.class);
        when(messageSource.getMessage(anyString(), any(Object[].class),any(Locale.class))).thenReturn("");
        delegatingMessageSource.setParentMessageSource(messageSource);
    }

    @Test
    @WithMockUser
    void testAddBook() throws Exception {
        List<String> categories = new ArrayList<>();
        EBookLabel label = EBookLabel.TEMPORARY_SWAP;

        BookData newBook = new BookData();
        newBook.setCategories(categories);
        newBook.setLabel(label);

        Book book = modelMapper.map(newBook, Book.class);

        MockMultipartFile info = new MockMultipartFile("info", "",
                "application/json", objectMapper.writeValueAsBytes(newBook));

        Mockito.doNothing().when(testUserBooksService).addBook(book, categories, label);

        mockMvc.perform(MockMvcRequestBuilders.multipart("/user-books/book")
                .file(info))
                .andExpect(status().isOk());

        verify(testUserBooksService).addBook(book, categories, label);
    }

    @Test
    @WithMockUser
    void testAddBookWithImage() throws Exception {
        List<String> categories = new ArrayList<>();
        EBookLabel label = EBookLabel.TEMPORARY_SWAP;

        BookData newBook = new BookData();
        newBook.setCategories(categories);
        newBook.setLabel(label);

        Book book = modelMapper.map(newBook, Book.class);

        MockMultipartFile info = new MockMultipartFile("info", "",
                "application/json", objectMapper.writeValueAsBytes(newBook));
        MockMultipartFile image = new MockMultipartFile("image", new byte[1]);

        Mockito.doNothing().when(testUserBooksService).addBook(image, book, categories, label);

        mockMvc.perform(MockMvcRequestBuilders.multipart("/user-books/book")
                        .file(image)
                        .file(info))
                .andExpect(status().isOk());

        verify(testUserBooksService).addBook(image, book, categories, label);
    }

    @Test
    @WithMockUser
    void testAddBookWithImageThrowIOException() throws Exception {
        List<String> categories = new ArrayList<>();
        EBookLabel label = EBookLabel.TEMPORARY_SWAP;

        BookData newBook = new BookData();
        newBook.setCategories(categories);
        newBook.setLabel(label);

        Book book = modelMapper.map(newBook, Book.class);

        MockMultipartFile info = new MockMultipartFile("info", "",
                "application/json", objectMapper.writeValueAsBytes(newBook));
        MockMultipartFile image = new MockMultipartFile("image", new byte[1]);

        Mockito.doThrow(new IOException()).when(testUserBooksService).addBook(image, book, categories, label);

        mockMvc.perform(MockMvcRequestBuilders.multipart("/user-books/book")
                        .file(image)
                        .file(info))
                .andExpect(status().isExpectationFailed());

    }

    @Test
    @WithMockUser
    void testGetBookDetails() throws Exception {
        Long bookId = 1L;
        Book book = new Book();
        book.setId(bookId);
        book.setCategories(null);
        BookDetails bookDetails = new BookDetails();

        when(testUserBooksService.getBook(bookId)).thenReturn(book);

        mockMvc.perform(get("/user-books/book-details/{bookId}", bookId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
//                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//                .andExpect(content().string(objectMapper.writeValueAsString(bookDetails)));

        verify(testUserBooksService).getBook(bookId);
    }

    @Test
    @WithMockUser
    void testGetBook() throws Exception {
        Long bookId = 1L;
        Book book = new Book();
        book.setId(bookId);
        book.setCategories(null);

        when(testUserBooksService.getBook(bookId)).thenReturn(book);

        mockMvc.perform(get("/user-books/book/{bookId}", bookId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(testUserBooksService).getBook(bookId);
    }

    @Test
    @WithMockUser
    void testFilterBooks() throws Exception {
        BookFilter bookFilter = new BookFilter();
        List<BookListItem> bookItemList = new ArrayList<>();

        when(testUserBooksService.filterBooks(bookFilter)).thenReturn(bookItemList);

        mockMvc.perform(post("/user-books/books/filter")
                        .content(objectMapper.writeValueAsString(bookFilter))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(objectMapper.writeValueAsString(bookItemList)));

        verify(testUserBooksService).filterBooks(bookFilter);
    }

    @Test
    @WithMockUser
    void testLoadFilterHints() throws Exception {
        FilterHints filterHints = new FilterHints();

        when(testUserBooksService.loadFilterHints()).thenReturn(filterHints);

        mockMvc.perform(get("/user-books/filter-hints")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(objectMapper.writeValueAsString(filterHints)));

        verify(testUserBooksService).loadFilterHints();
    }

    @Test
    @WithMockUser
    void testLoadCategoriesNames() throws Exception {
        List<String> categories = new ArrayList<>();

        when(testUserBooksService.loadCategoriesNames()).thenReturn(categories);

        mockMvc.perform(get("/user-books/categories")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(objectMapper.writeValueAsString(categories)));

        verify(testUserBooksService).loadCategoriesNames();
    }

    @Test
    @WithMockUser
    void testUpdateBook() throws Exception {
        List<String> categories = new ArrayList<>();
        EBookLabel label = EBookLabel.TEMPORARY_SWAP;

        Long bookId = 1L;
        BookData bookData = new BookData();
        bookData.setCategories(categories);
        bookData.setLabel(label);

        MockMultipartFile info = new MockMultipartFile("info", "",
                "application/json", objectMapper.writeValueAsBytes(bookData));

        Mockito.doNothing().when(testUserBooksService).updateBook(bookId, bookData, categories, label);

        MockMultipartHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.multipart("/user-books/book/{id}", bookId);
        builder.with(new RequestPostProcessor() {
            @Override
            public MockHttpServletRequest postProcessRequest(MockHttpServletRequest request) {
                request.setMethod("PUT");
                return request;
            }
        });

        mockMvc.perform(builder
                        .file(info))
                .andExpect(status().isOk());

        verify(testUserBooksService).updateBook(bookId, bookData, categories, label);
    }

    @Test
    @WithMockUser
    void testUpdateBookWithImage() throws Exception {
        List<String> categories = new ArrayList<>();
        EBookLabel label = EBookLabel.TEMPORARY_SWAP;

        Long bookId = 1L;
        BookData bookData = new BookData();
        bookData.setCategories(categories);
        bookData.setLabel(label);

        MockMultipartFile info = new MockMultipartFile("info", "",
                "application/json", objectMapper.writeValueAsBytes(bookData));
        MockMultipartFile image = new MockMultipartFile("image", new byte[1]);

        Mockito.doNothing().when(testUserBooksService).updateBook(bookId, image, bookData, categories, label);

        MockMultipartHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.multipart("/user-books/book/{id}", bookId);
        builder.with(new RequestPostProcessor() {
            @Override
            public MockHttpServletRequest postProcessRequest(MockHttpServletRequest request) {
                request.setMethod("PUT");
                return request;
            }
        });

        mockMvc.perform(builder
                        .file(info)
                        .file(image))
                .andExpect(status().isOk());

        verify(testUserBooksService).updateBook(bookId, image, bookData, categories, label);
    }

    @Test
    @WithMockUser
    void testUpdateBookWithImageThrowIOException() throws Exception {
        List<String> categories = new ArrayList<>();
        EBookLabel label = EBookLabel.TEMPORARY_SWAP;

        Long bookId = 1L;
        BookData bookData = new BookData();
        bookData.setCategories(categories);
        bookData.setLabel(label);

        MockMultipartFile info = new MockMultipartFile("info", "",
                "application/json", objectMapper.writeValueAsBytes(bookData));
        MockMultipartFile image = new MockMultipartFile("image", new byte[1]);

        Mockito.doThrow(new IOException()).when(testUserBooksService)
                .updateBook(bookId, image, bookData, categories, label);

        MockMultipartHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.multipart("/user-books/book/{id}", bookId);
        builder.with(new RequestPostProcessor() {
            @Override
            public MockHttpServletRequest postProcessRequest(MockHttpServletRequest request) {
                request.setMethod("PUT");
                return request;
            }
        });

        mockMvc.perform(builder
                        .file(info)
                        .file(image))
                .andExpect(status().isExpectationFailed());

    }

}