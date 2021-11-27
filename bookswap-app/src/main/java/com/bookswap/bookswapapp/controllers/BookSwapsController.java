package com.bookswap.bookswapapp.controllers;

import com.bookswap.bookswapapp.services.BookSwapsService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/book-swaps")
@CrossOrigin("http://localhost:4200")
public class BookSwapsController {

    private final BookSwapsService bookSwapsService;
    private ModelMapper modelMapper;
    private static final Logger logger = LoggerFactory.getLogger(BookSwapsController.class);

    @Autowired
    public BookSwapsController(BookSwapsService bookSwapsService, ModelMapper modelMapper) {
        this.bookSwapsService = bookSwapsService;
        this.modelMapper = modelMapper;
    }


}
