package com.bookswap.bookswapapp.controllers.manageusers;

import com.bookswap.bookswapapp.controllers.UserBooksController;
import com.bookswap.bookswapapp.dtos.auth.MessageResponse;
import com.bookswap.bookswapapp.dtos.manageusers.ProfileData;
import com.bookswap.bookswapapp.dtos.manageusers.UpdateUserData;
import com.bookswap.bookswapapp.models.User;
import com.bookswap.bookswapapp.services.UsersService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    public UsersController(UsersService usersService, ModelMapper modelMapper) {
        this.usersService = usersService;
        this.modelMapper = modelMapper;
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
            message = "Pomyśnie zaktualizowano profil użytkownika";
        } else {
            message = "Profil użytkownika zawierał już takie same dane";
        }
        return ResponseEntity.ok(new MessageResponse(message));
    }

    private ProfileData mapUserToProfileData(User user){
        return modelMapper.map(user, ProfileData.class);
    }

}
