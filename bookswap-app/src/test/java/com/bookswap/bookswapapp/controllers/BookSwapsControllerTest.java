package com.bookswap.bookswapapp.controllers;

import com.bookswap.bookswapapp.dtos.bookswaps.SwapFilter;
import com.bookswap.bookswapapp.dtos.bookswaps.SwapListItem;
import com.bookswap.bookswapapp.dtos.bookswaps.SwapsResponse;
import com.bookswap.bookswapapp.enums.EBookLabel;
import com.bookswap.bookswapapp.enums.ESwapStatus;
import com.bookswap.bookswapapp.models.User;
import com.bookswap.bookswapapp.security.jwt.AuthEntryPointJwt;
import com.bookswap.bookswapapp.security.jwt.JwtUtils;
import com.bookswap.bookswapapp.security.userdetails.UserDetailsServiceI;
import com.bookswap.bookswapapp.services.BookSwapsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookSwapsController.class)
class BookSwapsControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookSwapsService testBookSwapsService;

    @MockBean
    private ModelMapper modelMapper;

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
    }

    @Test
    @WithMockUser
    void testGetSwaps() throws Exception {

        SwapFilter swapFilter = new SwapFilter();
        swapFilter.setBookLabel(EBookLabel.TEMPORARY_SWAP);
        ESwapStatus status = ESwapStatus.IN_PROGRESS;
        List<ESwapStatus> statuses = new ArrayList<>();
        statuses.add(status);
        swapFilter.setSwapStatus(statuses);

        SwapsResponse swapsResponse = new SwapsResponse();

        when(testBookSwapsService.getSwaps(swapFilter, 0, 10))
                .thenReturn(swapsResponse);

        mockMvc.perform(post("/book-swaps/swaps")
                        .content(objectMapper.writeValueAsString(swapFilter))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(objectMapper.writeValueAsString(swapsResponse)));

        verify(testBookSwapsService).getSwaps(swapFilter, 0, 10);
    }

    @Test
    @WithMockUser
    void testGetUserAddressByUsername() throws Exception {
        String username = "username";
        String email = "test1@gmail.com";
        User user = new User();
        user.setUsername(username);
        user.setPassword("password");
        user.setEmail(email);

        when(testBookSwapsService.geUserAddressByUsername(username))
                .thenReturn(user);

        mockMvc.perform(get("/book-swaps/user-address/{username}", username)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(testBookSwapsService).geUserAddressByUsername(username);
    }

    @Test
    @WithMockUser
    void testConfirmBookDelivery() throws Exception {
        Long swapId = 1L;

        SwapListItem swapListItem = new SwapListItem();
        swapListItem.setId(swapId);

        when(testBookSwapsService.confirmBookDelivery(swapId))
                .thenReturn(swapListItem);

        mockMvc.perform(put("/book-swaps/swap/confirm/{swapId}", swapId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(objectMapper.writeValueAsString(swapListItem)));

        verify(testBookSwapsService).confirmBookDelivery(swapId);
    }
}