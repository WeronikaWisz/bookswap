package com.bookswap.bookswapapp.controllers;

import com.bookswap.bookswapapp.dtos.auth.MessageResponse;
import com.bookswap.bookswapapp.dtos.userbooks.NewBook;
import com.bookswap.bookswapapp.models.Book;
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
        modelMapper.addMappings(new PropertyMap<NewBook, Book>() {
            @Override
            protected void configure() {
                skip(destination.getId());
                skip(destination.getCategories());
                skip(destination.getLabel());
            }
        });
    }

    @PostMapping(path = "/book", consumes = {
            MediaType.APPLICATION_JSON_VALUE,
            MediaType.MULTIPART_FORM_DATA_VALUE
    })
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> addBook(@RequestPart("image") MultipartFile image, @RequestPart("info") NewBook newBook) {
        try {
            this.userBooksService.addBook(image, mapNewBookToBook(newBook), newBook.getCategories(), newBook.getLabel());
        } catch (IOException e){
            logger.error("Error getting bytes from file: ", e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.EXPECTATION_FAILED)
                    .body(new MessageResponse("Nie można zapisać pliku"));
        }
        return ResponseEntity.ok(new MessageResponse("Pomyśnie dodano książkę"));
    }

    private Book mapNewBookToBook(NewBook newBook){
        return this.modelMapper.map(newBook, Book.class);
    }

}
