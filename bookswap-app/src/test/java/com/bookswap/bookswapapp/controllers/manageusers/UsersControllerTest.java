package com.bookswap.bookswapapp.controllers.manageusers;

import com.bookswap.bookswapapp.dtos.manageusers.ChangePassword;
import com.bookswap.bookswapapp.dtos.manageusers.UpdateUserData;
import com.bookswap.bookswapapp.models.User;
import com.bookswap.bookswapapp.security.jwt.AuthEntryPointJwt;
import com.bookswap.bookswapapp.security.jwt.JwtUtils;
import com.bookswap.bookswapapp.security.userdetails.UserDetailsServiceI;
import com.bookswap.bookswapapp.services.UsersService;
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

import java.util.Locale;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UsersController.class)
class UsersControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UsersService testUsersService;

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
    void testGetUserProfileData() throws Exception {
        User user = new User();
        user.setUsername("username");
        user.setPassword("password");
        user.setEmail("test1@gmail.com");

        when(testUsersService.getUserProfileData()).thenReturn(user);

        mockMvc.perform(get("/users/user")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(testUsersService).getUserProfileData();
    }

    @Test
    @WithMockUser
    void testUpdateUserProfileData() throws Exception{
        UpdateUserData updateUserData= new UpdateUserData();
        when(testUsersService.updateUserProfileData(updateUserData)).thenReturn(true);

        mockMvc.perform(put("/users/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateUserData)))
                .andExpect(status().isOk());

        verify(testUsersService).updateUserProfileData(updateUserData);
    }

    @Test
    @WithMockUser
    void testUpdateUserProfileDataNorChanged() throws Exception{
        UpdateUserData updateUserData= new UpdateUserData();
        when(testUsersService.updateUserProfileData(updateUserData)).thenReturn(false);

        mockMvc.perform(put("/users/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateUserData)))
                .andExpect(status().isExpectationFailed());

        verify(testUsersService).updateUserProfileData(updateUserData);
    }

    @Test
    @WithMockUser
    void testChangePassword() throws Exception {
        ChangePassword changePassword = new ChangePassword();

        Mockito.doNothing().when(testUsersService).changePassword(changePassword);

        mockMvc.perform(put("/users/user/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(changePassword)))
                .andExpect(status().isOk());

        verify(testUsersService).changePassword(changePassword);
    }
}