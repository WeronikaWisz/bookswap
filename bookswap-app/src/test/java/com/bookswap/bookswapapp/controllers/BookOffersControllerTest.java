package com.bookswap.bookswapapp.controllers;

import com.bookswap.bookswapapp.dtos.bookoffers.*;
import com.bookswap.bookswapapp.security.jwt.AuthEntryPointJwt;
import com.bookswap.bookswapapp.security.jwt.JwtUtils;
import com.bookswap.bookswapapp.security.userdetails.UserDetailsServiceI;
import com.bookswap.bookswapapp.services.BookOffersService;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(BookOffersController.class)
class BookOffersControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookOffersService testBookOffersService;

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
    void testGetOfferDetails() throws Exception {
        Long offerId = 1L;
        OfferDetails offerDetails = new OfferDetails();

        when(testBookOffersService.getOffer(offerId)).thenReturn(offerDetails);

        mockMvc.perform(get("/book-offers/offer-details/{offerId}", offerId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(objectMapper.writeValueAsString(offerDetails)));

        verify(testBookOffersService).getOffer(offerId);
    }

    @Test
    @WithMockUser
    void testFilterOffers() throws Exception {
        OfferFilter offerFilter = new OfferFilter();
        OffersResponse offersResponse = new OffersResponse();

        when(testBookOffersService.filterOffers(offerFilter)).thenReturn(offersResponse);

        mockMvc.perform(post("/book-offers/offers/filter")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(offerFilter)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(objectMapper.writeValueAsString(offersResponse)));

        verify(testBookOffersService).filterOffers(offerFilter);
    }

    @Test
    @WithMockUser
    void testLoadFilterHints() throws Exception {
        FilterHints filterHints = new FilterHints();
        when(testBookOffersService.loadFilterHints()).thenReturn(filterHints);

        mockMvc.perform(get("/book-offers/filter-hints")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(objectMapper.writeValueAsString(filterHints)));

        verify(testBookOffersService).loadFilterHints();
    }

    @Test
    @WithMockUser
    void testSendSwapRequest() throws Exception {
        BooksForSwap booksForSwap = new BooksForSwap();

        Mockito.doNothing().when(testBookOffersService).sendSwapRequest(booksForSwap);

        mockMvc.perform(post("/book-offers/swap-request")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(booksForSwap)))
                .andExpect(status().isOk());

        verify(testBookOffersService).sendSwapRequest(booksForSwap);
    }

    @Test
    @WithMockUser
    void testGetSentRequests() throws Exception {
        SwapRequestFilter swapRequestFilter = new SwapRequestFilter();
        List<SwapRequestListItem> swapRequestListItems = new ArrayList<>();
        when(testBookOffersService.getSentRequests(swapRequestFilter)).thenReturn(swapRequestListItems);

        mockMvc.perform(post("/book-offers/sent-requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(swapRequestFilter)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(objectMapper.writeValueAsString(swapRequestListItems)));

        verify(testBookOffersService).getSentRequests(swapRequestFilter);
    }

    @Test
    @WithMockUser
    void testGetReceivedRequests() throws Exception {
        SwapRequestFilter swapRequestFilter = new SwapRequestFilter();
        List<SwapRequestListItem> swapRequestListItems = new ArrayList<>();
        when(testBookOffersService.getReceivedRequests(swapRequestFilter)).thenReturn(swapRequestListItems);

        mockMvc.perform(post("/book-offers/received-requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(swapRequestFilter)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(objectMapper.writeValueAsString(swapRequestListItems)));

        verify(testBookOffersService).getReceivedRequests(swapRequestFilter);
    }

    @Test
    @WithMockUser
    void testCancelSwapRequest() throws Exception {
        Long swapRequestId = 1L;

        Mockito.doNothing().when(testBookOffersService).cancelSwapRequest(swapRequestId);

        mockMvc.perform(delete("/book-offers/swap-request/cancel/{swapRequestId}", swapRequestId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(testBookOffersService).cancelSwapRequest(swapRequestId);
    }

    @Test
    @WithMockUser
    void testDenySwapRequest() throws Exception {
        Long swapRequestId = 1L;

        Mockito.doNothing().when(testBookOffersService).denySwapRequest(swapRequestId);

        mockMvc.perform(delete("/book-offers/swap-request/deny/{swapRequestId}", swapRequestId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(testBookOffersService).denySwapRequest(swapRequestId);
    }
}