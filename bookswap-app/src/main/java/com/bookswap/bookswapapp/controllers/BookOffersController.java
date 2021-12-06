package com.bookswap.bookswapapp.controllers;

import com.bookswap.bookswapapp.dtos.auth.MessageResponse;
import com.bookswap.bookswapapp.dtos.bookoffers.*;
import com.bookswap.bookswapapp.services.BookOffersService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/book-offers")
@CrossOrigin("http://localhost:4200")
public class BookOffersController {

    private final BookOffersService bookOffersService;
    private ModelMapper modelMapper;
    private static final Logger logger = LoggerFactory.getLogger(UserBooksController.class);
    private MessageSource messageSource;

    @Autowired
    public BookOffersController(BookOffersService bookOffersService, ModelMapper modelMapper,
                                MessageSource messageSource) {
        this.bookOffersService = bookOffersService;
        this.modelMapper = modelMapper;
        this.messageSource = messageSource;
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
        bookOffersService.sendSwapRequest(booksForSwap);
        return ResponseEntity.ok(new MessageResponse(messageSource.getMessage(
                "success.sendRequest", null, LocaleContextHolder.getLocale())));
    }

    @PostMapping(path = "/sent-requests")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> getSentRequests(@RequestBody SwapRequestFilter swapRequestFilter) {
        List<SwapRequestListItem> swapRequestListItems = bookOffersService.getSentRequests(swapRequestFilter);
        return ResponseEntity.ok(swapRequestListItems);
    }

    @PostMapping(path = "/received-requests")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> getReceivedRequests(@RequestBody SwapRequestFilter swapRequestFilter) {
        List<SwapRequestListItem> swapRequestListItems = bookOffersService.getReceivedRequests(swapRequestFilter);
        return ResponseEntity.ok(swapRequestListItems);
    }

    @DeleteMapping(path = "/swap-request/cancel/{swapRequestId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> cancelSwapRequest(@PathVariable("swapRequestId") Long id) {
        bookOffersService.cancelSwapRequest(id);
        return ResponseEntity.ok(new MessageResponse(messageSource.getMessage(
                "success.cancelRequest", null, LocaleContextHolder.getLocale())));
    }

    @DeleteMapping(path = "/swap-request/deny/{swapRequestId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> denySwapRequest(@PathVariable("swapRequestId") Long id) {
        bookOffersService.denySwapRequest(id);
        return ResponseEntity.ok(new MessageResponse(messageSource.getMessage(
                "success.denyRequest", null, LocaleContextHolder.getLocale())));
    }

}
