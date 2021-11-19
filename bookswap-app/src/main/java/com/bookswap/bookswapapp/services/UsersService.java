package com.bookswap.bookswapapp.services;

import com.bookswap.bookswapapp.models.User;
import com.bookswap.bookswapapp.repositories.UserRepository;
import com.bookswap.bookswapapp.security.userdetails.UserDetailsI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UsersService {

    private final UserRepository userRepository;
    private static final Logger logger = LoggerFactory.getLogger(UserBooksService.class);

    @Autowired
    public UsersService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getUserProfileData(){
        UserDetailsI userDetails = (UserDetailsI) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        String username = userDetails.getUsername();
        return userRepository.findByUsername(username).orElseThrow(
                () -> new UsernameNotFoundException("Cannot found user"));
    }

}
