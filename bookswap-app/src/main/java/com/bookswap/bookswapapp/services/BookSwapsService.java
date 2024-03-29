package com.bookswap.bookswapapp.services;

import com.bookswap.bookswapapp.dtos.bookswaps.SwapFilter;
import com.bookswap.bookswapapp.dtos.bookswaps.SwapListItem;
import com.bookswap.bookswapapp.dtos.bookswaps.SwapsResponse;
import com.bookswap.bookswapapp.dtos.userbooks.BooksResponse;
import com.bookswap.bookswapapp.enums.EBookLabel;
import com.bookswap.bookswapapp.enums.EBookStatus;
import com.bookswap.bookswapapp.enums.ESwapStatus;
import com.bookswap.bookswapapp.exception.ApiNotFoundException;
import com.bookswap.bookswapapp.helpers.ImageHelper;
import com.bookswap.bookswapapp.models.Book;
import com.bookswap.bookswapapp.models.Swap;
import com.bookswap.bookswapapp.models.User;
import com.bookswap.bookswapapp.repositories.*;
import com.bookswap.bookswapapp.security.userdetails.UserDetailsI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookSwapsService {

    private final UserRepository userRepository;
    private final SwapRepository swapRepository;
    private static final Logger logger = LoggerFactory.getLogger(BookSwapsService.class);

    @Autowired
    public BookSwapsService(UserRepository userRepository, SwapRepository swapRepository) {
        this.userRepository = userRepository;
        this.swapRepository = swapRepository;
    }

    @Transactional
    public SwapsResponse getSwaps(SwapFilter swapFilter, Integer page, Integer size){
        List<SwapListItem> swapListItems = new ArrayList<>();
        User user = getCurrentUser();
        List<Swap> swaps = swapRepository.findUserSwaps(swapFilter.getSwapStatus(),
                swapFilter.getBookLabel(), user).orElse(Collections.emptyList());
        logger.info("Label: " + swapFilter.getBookLabel());
        swaps = swaps.stream()
                .sorted(Comparator.comparing(Swap::getCreationDate).reversed()).collect(Collectors.toList());
        for(Swap swap: swaps){
            SwapListItem swapListItem = new SwapListItem();
            swapListItem.setId(swap.getId());
            swapListItem.setSwapStatus(swap.getStatus());
            if(swap.getStatus().equals(ESwapStatus.IN_PROGRESS)){
                swapListItem.setIfCurrentUserConfirmed(false);
            } else if(swap.getStatus().equals(ESwapStatus.COMPLETED)){
                swapListItem.setIfCurrentUserConfirmed(true);
            } else if(swap.getStatus().equals(ESwapStatus.BOTH_CONFIRMED)){
                swapListItem.setIfCurrentUserConfirmed(false);
            }
            if(swap.getBook1().getUser().getUsername().equals(user.getUsername())){
                setBooksInSwapsListItem(swapListItem, swap.getBook1(), swap.getBook2());
                if(swap.getStatus().equals(ESwapStatus.BOOK_1_CONFIRMED)
                        || swap.getStatus().equals(ESwapStatus.BOOK_2_RETURNED)){
                    swapListItem.setIfCurrentUserConfirmed(false);
                } else if(swap.getStatus().equals(ESwapStatus.BOOK_2_CONFIRMED)
                        || swap.getStatus().equals(ESwapStatus.BOOK_1_RETURNED)) {
                    swapListItem.setIfCurrentUserConfirmed(true);
                }
            } else {
                setBooksInSwapsListItem(swapListItem, swap.getBook2(), swap.getBook1());
                if(swap.getStatus().equals(ESwapStatus.BOOK_1_CONFIRMED)
                        || swap.getStatus().equals(ESwapStatus.BOOK_2_RETURNED)){
                    swapListItem.setIfCurrentUserConfirmed(true);
                } else if(swap.getStatus().equals(ESwapStatus.BOOK_2_CONFIRMED)
                        || swap.getStatus().equals(ESwapStatus.BOOK_1_RETURNED)) {
                    swapListItem.setIfCurrentUserConfirmed(false);
                }
            }
            swapListItems.add(swapListItem);
        }
        int total = swapListItems.size();
        int start = page * size;
        int end = Math.min(start + size, total);
        SwapsResponse swapsResponse = new SwapsResponse();
        if(end >= start){
            swapsResponse.setSwapsList(swapListItems.subList(start, end));
        }
        swapsResponse.setTotalSwapsLength(total);
        return swapsResponse;
    }

    public User geUserAddressByUsername(String username){
        return userRepository.findByUsername(username).orElseThrow(
                () -> new ApiNotFoundException("exception.usernameNotExists")
        );
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public SwapListItem confirmBookDelivery(Long swapId){
        Swap swap = swapRepository.findById(swapId).orElseThrow(
                () -> new ApiNotFoundException("exception.swapNotExists")
        );
        User user = getCurrentUser();
        ESwapStatus status = swap.getStatus();
        EBookLabel label = swap.getBook1().getLabel();
        SwapListItem swapListItem = new SwapListItem();
        swapListItem.setId(swap.getId());
        if(swap.getBook1().getUser().getUsername().equals(user.getUsername())){
            if(status.equals(ESwapStatus.IN_PROGRESS)){
                swap.setStatus(ESwapStatus.BOOK_2_CONFIRMED);
            } else if (status.equals(ESwapStatus.BOOK_1_CONFIRMED)){
                if (label.equals(EBookLabel.PERMANENT_SWAP)) {
                    swap.setStatus(ESwapStatus.COMPLETED);
                } else {
                    swap.setStatus(ESwapStatus.BOTH_CONFIRMED);
                }
            } else if(status.equals(ESwapStatus.BOTH_CONFIRMED)){
                swap.setStatus(ESwapStatus.BOOK_1_RETURNED);
                swap.getBook1().setStatus(EBookStatus.AVAILABLE);
            } else if(status.equals(ESwapStatus.BOOK_2_RETURNED)){
                swap.setStatus(ESwapStatus.COMPLETED);
                swap.getBook1().setStatus(EBookStatus.AVAILABLE);
            }
            setBooksInSwapsListItem(swapListItem, swap.getBook1(), swap.getBook2());
        } else {
            if(status.equals(ESwapStatus.IN_PROGRESS)){
                swap.setStatus(ESwapStatus.BOOK_1_CONFIRMED);
            } else if (status.equals(ESwapStatus.BOOK_2_CONFIRMED)){
                if(label.equals(EBookLabel.PERMANENT_SWAP)) {
                    swap.setStatus(ESwapStatus.COMPLETED);
                } else {
                    swap.setStatus(ESwapStatus.BOTH_CONFIRMED);
                }
            } else if(status.equals(ESwapStatus.BOTH_CONFIRMED)){
                swap.setStatus(ESwapStatus.BOOK_2_RETURNED);
                swap.getBook2().setStatus(EBookStatus.AVAILABLE);
            } else if(status.equals(ESwapStatus.BOOK_1_RETURNED)){
                swap.setStatus(ESwapStatus.COMPLETED);
                swap.getBook2().setStatus(EBookStatus.AVAILABLE);
            }
            setBooksInSwapsListItem(swapListItem, swap.getBook2(), swap.getBook1());
        }
        swapListItem.setSwapStatus(swap.getStatus());
        swapListItem.setIfCurrentUserConfirmed(true);

        return swapListItem;
    }

    private void setBooksInSwapsListItem(SwapListItem swapListItem, Book currentUserBook, Book otherUserBook){
        swapListItem.setCurrentUserBookTitle(currentUserBook.getTitle());
        swapListItem.setCurrentUserBookAuthor(currentUserBook.getAuthor());
        swapListItem.setCurrentUserBookLabel(currentUserBook.getLabel());
        if (currentUserBook.getImage() != null) {
            swapListItem.setCurrentUserBookImage(ImageHelper.decompressBytes(currentUserBook.getImage()));
        }
        swapListItem.setOtherUserBookTitle(otherUserBook.getTitle());
        swapListItem.setOtherUserBookAuthor(otherUserBook.getAuthor());
        swapListItem.setOtherUserBookLabel(otherUserBook.getLabel());
        if (otherUserBook.getImage() != null) {
            swapListItem.setOtherUserBookImage(ImageHelper.decompressBytes(otherUserBook.getImage()));
        }
        swapListItem.setOtherUsername(otherUserBook.getUser().getUsername());
    }

    private User getCurrentUser(){
        UserDetailsI userDetails = (UserDetailsI) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        String username = userDetails.getUsername();
        return userRepository.findByUsername(username).orElseThrow(
                () -> new UsernameNotFoundException("Cannot found user"));
    }
}
