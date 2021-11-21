package com.bookswap.bookswapapp.controllers;

import com.bookswap.bookswapapp.dtos.auth.MessageResponse;
import com.bookswap.bookswapapp.dtos.bookoffers.*;
import com.bookswap.bookswapapp.enums.EBookLabel;
import com.bookswap.bookswapapp.services.BookOffersService;
import org.modelmapper.ModelMapper;
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
@RequestMapping("/book-offers")
@CrossOrigin("http://localhost:4200")
public class BookOffersController {

    private final BookOffersService bookOffersService;
    private ModelMapper modelMapper;
    private static final Logger logger = LoggerFactory.getLogger(UserBooksController.class);

    @Autowired
    public BookOffersController(BookOffersService bookOffersService, ModelMapper modelMapper) {
        this.bookOffersService = bookOffersService;
        this.modelMapper = modelMapper;
    }

    @GetMapping(path = "/offers")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> loadOffers(@RequestParam EBookLabel label) {
        OffersResponse offersResponse = bookOffersService.loadOffers(label);
        return ResponseEntity.ok(offersResponse);
    }

    @GetMapping(path = "/offer-details/{offerId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> getOfferDetails(@PathVariable("offerId") Long id) {
        OfferDetails offerDetails = bookOffersService.getOffer(id);
        return ResponseEntity.ok(offerDetails);
    }

    @PostMapping(path = "/offers/filter")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> filterOffers(@RequestBody OfferFilter offerFilter) {
        OffersResponse offersResponse = bookOffersService.filterOffers(offerFilter);
        return ResponseEntity.ok(offersResponse);
    }

    @GetMapping(path = "/filter-hints")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> loadFilterHints() {
        FilterHints filterHints = bookOffersService.loadFilterHints();
        return ResponseEntity.ok(filterHints);
    }

    @PostMapping(path = "/swap-request")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> sendSwapRequest(@RequestBody BooksForSwap booksForSwap) {
        logger.info("hello");
        bookOffersService.sendSwapRequest(booksForSwap);
        return ResponseEntity.ok(new MessageResponse("Pomyśnie złożono propozycję"));
    }

}
