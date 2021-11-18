package com.bookswap.bookswapapp.controllers;

import com.bookswap.bookswapapp.dtos.auth.MessageResponse;
import com.bookswap.bookswapapp.dtos.userbooks.*;
import com.bookswap.bookswapapp.enums.EBookStatus;
import com.bookswap.bookswapapp.models.Book;
import com.bookswap.bookswapapp.models.Category;
import com.bookswap.bookswapapp.services.UserBooksService;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/user-books")
@CrossOrigin("http://localhost:4200")
public class UserBooksController {

    private final UserBooksService userBooksService;
    private ModelMapper modelMapper;
    private static final Logger logger = LoggerFactory.getLogger(UserBooksController.class);

    @Autowired
    public UserBooksController(UserBooksService userBooksService, ModelMapper modelMapper) {
        this.userBooksService = userBooksService;
        this.modelMapper = modelMapper;
        modelMapper.addMappings(new PropertyMap<BookData, Book>() {
            @Override
            protected void configure() {
                skip(destination.getId());
                skip(destination.getCategories());
                skip(destination.getLabel());
            }
        });
        modelMapper.addMappings(new PropertyMap<Book, BookData>() {
            @Override
            protected void configure() {
                skip(destination.getCategories());
            }
        });
        modelMapper.addMappings(new PropertyMap<Book, BookDetails>() {
            @Override
            protected void configure() {
                skip(destination.getCategories());
            }
        });
    }

    @PostMapping(path = "/book", consumes = {
            MediaType.APPLICATION_JSON_VALUE,
            MediaType.MULTIPART_FORM_DATA_VALUE
    })
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> addBook(@RequestPart(name="image", required=false) MultipartFile image, @RequestPart("info") BookData newBook) {
        if(image!=null) {
            try {
                this.userBooksService.addBook(image, mapBookDataToBook(newBook), newBook.getCategories(), newBook.getLabel());
            } catch (IOException e) {
                logger.error("Error getting bytes from file: ", e.getMessage());
                return ResponseEntity
                        .status(HttpStatus.EXPECTATION_FAILED)
                        .body(new MessageResponse("Nie można zapisać pliku"));
            }
        } else {
            this.userBooksService.addBook(mapBookDataToBook(newBook), newBook.getCategories(), newBook.getLabel());
        }
        return ResponseEntity.ok(new MessageResponse("Pomyśnie dodano książkę"));
    }

    @GetMapping(path = "/books")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> loadBooks(@RequestParam EBookStatus status) {
        List<BookListItem> bookItemList = userBooksService.loadBooks(status);
        return ResponseEntity.ok(bookItemList);
    }

    @GetMapping(path = "/book-details/{bookId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> getBookDetails(@PathVariable("bookId") Long id) {
        BookDetails bookDetails = mapBookToBookDetails(userBooksService.getBook(id));
        return ResponseEntity.ok(bookDetails);
    }

    @GetMapping(path = "/book/{bookId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> getBook(@PathVariable("bookId") Long id) {
        BookData bookData = mapBookToBookData(userBooksService.getBook(id));
        return ResponseEntity.ok(bookData);
    }

    @PostMapping(path = "/books/filter")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> filterBooks(@RequestBody BookFilter bookFilter) {
        List<BookListItem> bookItemList = userBooksService.filterBooks(bookFilter);
        return ResponseEntity.ok(bookItemList);
    }

    @GetMapping(path = "/filter-hints")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> loadFilterHints(@RequestParam EBookStatus status) {
        FilterHints filterHints = userBooksService.loadFilterHints(status);
        return ResponseEntity.ok(filterHints);
    }

    @GetMapping(path = "/categories")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> loadCategoriesNames() {
        List<String> categories = userBooksService.loadCategoriesNames();
        return ResponseEntity.ok(categories);
    }


    private Book mapBookDataToBook(BookData bookData){
        return this.modelMapper.map(bookData, Book.class);
    }

    private BookData mapBookToBookData(Book book){
        BookData bookData = this.modelMapper.map(book, BookData.class);
        if(book.getCategories() != null) {
            bookData.setCategories(book.getCategories().stream().map(Category::getName).collect(Collectors.toList()));
        }
        return bookData;

    }

    private BookDetails mapBookToBookDetails(Book book){
        BookDetails bookDetails = this.modelMapper.map(book, BookDetails.class);
        if(book.getCategories() != null) {
            bookDetails.setCategories(book.getCategories().stream().map(Category::getName).collect(Collectors.toList()));
        }
        return bookDetails;
    }

}
