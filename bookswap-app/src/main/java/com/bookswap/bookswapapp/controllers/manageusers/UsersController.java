package com.bookswap.bookswapapp.controllers.manageusers;

import com.bookswap.bookswapapp.controllers.UserBooksController;
import com.bookswap.bookswapapp.dtos.auth.MessageResponse;
import com.bookswap.bookswapapp.dtos.manageusers.ChangePassword;
import com.bookswap.bookswapapp.dtos.manageusers.ProfileData;
import com.bookswap.bookswapapp.dtos.manageusers.UpdateUserData;
import com.bookswap.bookswapapp.models.User;
import com.bookswap.bookswapapp.services.UsersService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@CrossOrigin("http://localhost:4200")
public class UsersController {

    private final UsersService usersService;
    private ModelMapper modelMapper;
    private static final Logger logger = LoggerFactory.getLogger(UserBooksController.class);
    private MessageSource messageSource;

    @Autowired
    public UsersController(UsersService usersService, ModelMapper modelMapper, MessageSource messageSource) {
        this.usersService = usersService;
        this.modelMapper = modelMapper;
        this.messageSource = messageSource;
    }

    @GetMapping(path = "/user")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> getUserProfileData() {
        ProfileData profileData = mapUserToProfileData(usersService.getUserProfileData());
        return ResponseEntity.ok(profileData);
    }

    @PutMapping(path = "/user")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> updateUserProfileData(@RequestBody UpdateUserData updateUserData) {
        boolean dataChanged = usersService.updateUserProfileData(updateUserData);
        String message;
        if(dataChanged) {
            message = messageSource.getMessage(
                    "success.profileUpdate", null, LocaleContextHolder.getLocale());
            return ResponseEntity.ok(new MessageResponse(message));
        } else {
            message = messageSource.getMessage(
                    "exception.profileSameData", null, LocaleContextHolder.getLocale());
            return ResponseEntity
                    .status(HttpStatus.EXPECTATION_FAILED)
                    .body(new MessageResponse(message));
        }
    }

    @PutMapping(path = "/user/password")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> changePassword(@RequestBody ChangePassword changePassword) {
        usersService.changePassword(changePassword);
        return ResponseEntity.ok(new MessageResponse(messageSource.getMessage(
                "success.passwordChange", null, LocaleContextHolder.getLocale())));
    }

    private ProfileData mapUserToProfileData(User user){
        return modelMapper.map(user, ProfileData.class);
    }

}
