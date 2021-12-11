package com.bookswap.bookswapapp.controllers.manageusers;

import com.bookswap.bookswapapp.dtos.auth.LoginRequest;
import com.bookswap.bookswapapp.dtos.auth.SignupRequest;
import com.bookswap.bookswapapp.enums.ERole;
import com.bookswap.bookswapapp.models.Role;
import com.bookswap.bookswapapp.models.User;
import com.bookswap.bookswapapp.repositories.RoleRepository;
import com.bookswap.bookswapapp.repositories.UserRepository;
import com.bookswap.bookswapapp.security.jwt.AuthEntryPointJwt;
import com.bookswap.bookswapapp.security.jwt.JwtUtils;
import com.bookswap.bookswapapp.security.userdetails.UserDetailsI;
import com.bookswap.bookswapapp.security.userdetails.UserDetailsServiceI;
import com.bookswap.bookswapapp.services.BookSwapsService;
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
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Locale;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

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
    AuthenticationManager authenticationManager;

    @MockBean
    UserRepository testUserRepository;

    @MockBean
    RoleRepository testRoleRepository;

    @MockBean
    private AuthEntryPointJwt authEntryPointJwt;

    @MockBean
    PasswordEncoder encoder;

    @MockBean
    private JwtUtils jwtUtils;

    private MessageSource messageSource;

    @Autowired
    private DelegatingMessageSource delegatingMessageSource;

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
    void testAuthenticateUser() throws Exception{

        Authentication authentication = Mockito.mock(Authentication.class);

        String username = "username";
        String password = "password";

        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setEmail("test1@gmail.com");

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername(username);
        loginRequest.setPassword(password);
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword());

        UserDetailsI applicationUser = UserDetailsI.build(user);

        when(authenticationManager.authenticate(authenticationToken)).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(applicationUser);

        mockMvc.perform(post("/auth/signin")
                        .content(objectMapper.writeValueAsString(loginRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(authenticationManager).authenticate(authenticationToken);
        verify(jwtUtils).generateJwtToken(authentication);
    }

    @Test
    void testRegisterUser() throws Exception{
        SignupRequest signUpRequest = new SignupRequest();
        signUpRequest.setName("username");
        signUpRequest.setEmail("test1@gmail.com");
        signUpRequest.setPassword("password");
        signUpRequest.setPhone("123456789");

        when(testUserRepository.existsByUsername(signUpRequest.getUsername())).thenReturn(false);
        when(testUserRepository.existsByEmail(signUpRequest.getEmail())).thenReturn(false);
        when(testRoleRepository.findByName(ERole.ROLE_USER)).thenReturn(java.util.Optional.of(new Role()));
        when(encoder.encode(signUpRequest.getPassword())).thenReturn("");

        mockMvc.perform(post("/auth/signup")
                        .content(objectMapper.writeValueAsString(signUpRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(testUserRepository).existsByUsername(signUpRequest.getUsername());
        verify(testUserRepository).existsByEmail(signUpRequest.getEmail());
        verify(testRoleRepository).findByName(ERole.ROLE_USER);

    }

    @Test
    void testRegisterUserUsernameExists() throws Exception{
        SignupRequest signUpRequest = new SignupRequest();
        signUpRequest.setName("username");

        when(testUserRepository.existsByUsername(signUpRequest.getUsername())).thenReturn(true);

        mockMvc.perform(post("/auth/signup")
                        .content(objectMapper.writeValueAsString(signUpRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

    }

    @Test
    void testRegisterUserEmailExists() throws Exception{
        SignupRequest signUpRequest = new SignupRequest();
        signUpRequest.setName("username");

        when(testUserRepository.existsByUsername(signUpRequest.getUsername())).thenReturn(false);
        when(testUserRepository.existsByEmail(signUpRequest.getEmail())).thenReturn(true);

        mockMvc.perform(post("/auth/signup")
                        .content(objectMapper.writeValueAsString(signUpRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

    }
}