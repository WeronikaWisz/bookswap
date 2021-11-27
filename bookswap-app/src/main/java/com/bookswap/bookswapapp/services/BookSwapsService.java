package com.bookswap.bookswapapp.services;

import com.bookswap.bookswapapp.repositories.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BookSwapsService {

    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final SwapRequestRepository swapRequestRepository;
    private final SwapRepository swapRepository;
    private static final Logger logger = LoggerFactory.getLogger(BookSwapsService.class);

    @Autowired
    public BookSwapsService(BookRepository bookRepository, UserRepository userRepository,
                            SwapRequestRepository swapRequestRepository, SwapRepository swapRepository) {
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
        this.swapRequestRepository = swapRequestRepository;
        this.swapRepository = swapRepository;
    }


}
