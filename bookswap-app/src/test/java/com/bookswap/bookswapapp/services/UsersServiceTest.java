package com.bookswap.bookswapapp.services;

import com.bookswap.bookswapapp.dtos.manageusers.ChangePassword;
import com.bookswap.bookswapapp.dtos.manageusers.UpdateUserData;
import com.bookswap.bookswapapp.exception.ApiBadRequestException;
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
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UsersServiceTest {

    @Mock
    private UserRepository testUserRepository;
    private UsersService testUserService;
    private User user;
    @Mock
    PasswordEncoder encoder;

    @BeforeEach
    void setUp() {
        testUserService = new UsersService(testUserRepository, encoder);
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

    @Test
    void testUpdateUserProfileData() {
        UpdateUserData updateUserData = new UpdateUserData();
        updateUserData.setEmail("test2@email.com");
        updateUserData.setName("test");
        updateUserData.setSurname("test");
        updateUserData.setPhone("123456789");
        updateUserData.setPostalCode("43-098");
        updateUserData.setPost("Post");
        updateUserData.setCity("City");
        updateUserData.setStreet("Street");
        updateUserData.setBuildingNumber("3");
        updateUserData.setDoorNumber("9");

        Mockito.when(testUserRepository.findByUsername("username")).thenReturn(java.util.Optional.of(user));
        testUserService.updateUserProfileData(updateUserData);
        verify(testUserRepository).save(any(User.class));
    }

    @Test
    void testUpdateUserProfileDataUserNotFound() {
        Mockito.when(testUserRepository.findByUsername("username")).thenReturn(java.util.Optional.empty());
        UpdateUserData updateUserData = new UpdateUserData();
        assertThrows(UsernameNotFoundException.class, () -> testUserService.updateUserProfileData(updateUserData));
    }

    @Test
    void testUpdateUserProfileDataExistByEmail() {
        String email = "test2@email.com";
        Mockito.when(testUserRepository.findByUsername("username")).thenReturn(java.util.Optional.of(user));
        Mockito.when(testUserRepository.existsByEmail("test2@email.com")).thenReturn(true);
        UpdateUserData updateUserData = new UpdateUserData();
        updateUserData.setEmail(email);
        assertThrows(ApiBadRequestException.class, () -> testUserService.updateUserProfileData(updateUserData));
    }

    @Test
    void testUpdateUserProfileDataEmptyPhoneNumber() {
        UpdateUserData updateUserData = new UpdateUserData();
        updateUserData.setPhone("");

        Mockito.when(testUserRepository.findByUsername("username")).thenReturn(java.util.Optional.of(user));
        testUserService.updateUserProfileData(updateUserData);
        verify(testUserRepository).save(any(User.class));
    }

    @Test
    void testChangePassword() {
        String oldPassword = "password";
        String newPassword = "passwordNew";
        ChangePassword changePassword = new ChangePassword();
        changePassword.setOldPassword(oldPassword);
        changePassword.setNewPassword(newPassword);

        Mockito.when(testUserRepository.findByUsername("username")).thenReturn(java.util.Optional.of(user));
        Mockito.when(encoder.matches(oldPassword, user.getPassword())).thenReturn(true);

        testUserService.changePassword(changePassword);
        verify(testUserRepository).save(any(User.class));
    }

    @Test
    void testChangePasswordSameNewOld() {
        String oldPassword = "password";
        String newPassword = "password";
        ChangePassword changePassword = new ChangePassword();
        changePassword.setOldPassword(oldPassword);
        changePassword.setNewPassword(newPassword);

        Mockito.when(testUserRepository.findByUsername("username")).thenReturn(java.util.Optional.of(user));
        Mockito.when(encoder.matches(oldPassword, user.getPassword())).thenReturn(true);

        assertThrows(ApiBadRequestException.class, () -> testUserService.changePassword(changePassword));
    }

    @Test
    void testChangePasswordUserNotFound() {
        Mockito.when(testUserRepository.findByUsername("username")).thenReturn(java.util.Optional.empty());
        ChangePassword changePassword = new ChangePassword();
        assertThrows(UsernameNotFoundException.class, () -> testUserService.changePassword(changePassword));
    }

    @Test
    void testChangePasswordWrongOld() {
        Mockito.when(testUserRepository.findByUsername("username")).thenReturn(java.util.Optional.of(user));
        String newPassword = "passwordNew";
        ChangePassword changePassword = new ChangePassword();
        changePassword.setOldPassword("passwordWrong");
        changePassword.setNewPassword(newPassword);
        assertThrows(ApiBadRequestException.class, () -> testUserService.changePassword(changePassword));
    }
}