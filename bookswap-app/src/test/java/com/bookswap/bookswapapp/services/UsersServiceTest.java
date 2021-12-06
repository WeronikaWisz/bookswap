package com.bookswap.bookswapapp.services;

import com.bookswap.bookswapapp.models.User;
import com.bookswap.bookswapapp.repositories.UserRepository;
import com.bookswap.bookswapapp.security.userdetails.UserDetailsI;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UsersServiceTest {

    @Mock
    private UserRepository testUserRepository;
    private UsersService testUserService;
    private User user;

    @BeforeEach
    void setUp() {
        testUserService = new UsersService(testUserRepository);
        user = new User();
        user.setUsername("username");
        user.setPassword("password");
        user.setEmail("test1@gmail.com");
        testUserRepository.save(user);
        UserDetailsI applicationUser = UserDetailsI.build(user);
        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        Mockito.when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(applicationUser);
    }

    @Test
    void testGetUserProfileData() {
        Mockito.when(testUserRepository.findByUsername("username")).thenReturn(java.util.Optional.of(user));
        testUserService.getUserProfileData();
        verify(testUserRepository).findByUsername("username");
    }

    @Test
    void testGetUserProfileDataNotFound() {
        Mockito.when(testUserRepository.findByUsername("username")).thenReturn(java.util.Optional.empty());
        assertThrows(UsernameNotFoundException.class, () -> testUserService.getUserProfileData());
    }

//    @Test
//    void testUpdateUserProfileData() {
//    }
//
//    @Test
//    void testChangePassword() {
//    }
}