package com.bookswap.bookswapapp.controllers;

import com.bookswap.bookswapapp.dtos.bookswaps.SwapFilter;
import com.bookswap.bookswapapp.dtos.bookswaps.SwapListItem;
import com.bookswap.bookswapapp.dtos.manageusers.ProfileData;
import com.bookswap.bookswapapp.models.User;
import com.bookswap.bookswapapp.services.BookSwapsService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/book-swaps")
@CrossOrigin("http://localhost:4200")
public class BookSwapsController {

    private final BookSwapsService bookSwapsService;
    private ModelMapper modelMapper;
    private static final Logger logger = LoggerFactory.getLogger(BookSwapsController.class);

    @Autowired
    public BookSwapsController(BookSwapsService bookSwapsService, ModelMapper modelMapper) {
        this.bookSwapsService = bookSwapsService;
        this.modelMapper = modelMapper;
    }

    @PostMapping(path = "/swaps")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> getSwaps(@RequestBody SwapFilter swapFilter) {
        List<SwapListItem> swapListItems = bookSwapsService.getSwaps(swapFilter);
        return ResponseEntity.ok(swapListItems);
    }

    @GetMapping(path = "/user-address/{username}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> getUserAddressByUsername(@PathVariable("username") String username) {
        ProfileData profileData = mapUserToProfileData(bookSwapsService.geUserAddressByUsername(username));
        return ResponseEntity.ok(profileData);
    }

    @PutMapping(path = "/swap/confirm/{swapId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> confirmBookDelivery(@PathVariable Long swapId) {
        SwapListItem swapListItem = bookSwapsService.confirmBookDelivery(swapId);
        return ResponseEntity.ok(swapListItem);
    }

    private ProfileData mapUserToProfileData(User user){
        return modelMapper.map(user, ProfileData.class);
    }
}
