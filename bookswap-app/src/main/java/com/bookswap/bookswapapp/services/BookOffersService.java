package com.bookswap.bookswapapp.services;

import com.bookswap.bookswapapp.dtos.bookoffers.FilterHints;
import com.bookswap.bookswapapp.dtos.bookoffers.OfferFilter;
import com.bookswap.bookswapapp.dtos.bookoffers.OfferListItem;
import com.bookswap.bookswapapp.dtos.bookoffers.OffersResponse;
import com.bookswap.bookswapapp.enums.EBookLabel;
import com.bookswap.bookswapapp.enums.EBookStatus;
import com.bookswap.bookswapapp.helpers.FilterHelper;
import com.bookswap.bookswapapp.helpers.ImageHelper;
import com.bookswap.bookswapapp.models.Book;
import com.bookswap.bookswapapp.models.User;
import com.bookswap.bookswapapp.repositories.BookRepository;
import com.bookswap.bookswapapp.repositories.SwapRequestRepository;
import com.bookswap.bookswapapp.repositories.UserRepository;
import com.bookswap.bookswapapp.security.userdetails.UserDetailsI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
public class BookOffersService {

    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final SwapRequestRepository swapRequestRepository;
    private static final Logger logger = LoggerFactory.getLogger(BookOffersService.class);

    @Autowired
    public BookOffersService(BookRepository bookRepository, UserRepository userRepository,
                             SwapRequestRepository swapRequestRepository) {
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
        this.swapRequestRepository = swapRequestRepository;
    }

    public OffersResponse loadOffers(EBookLabel label){
        User user = getCurrentUser();
        List<Book> offerList = bookRepository
                .findBookByStatusAndLabelAndUserIsNot(EBookStatus.AVAILABLE, label, user)
                .orElse(Collections.emptyList());
        OffersResponse offersResponse = new OffersResponse();
        offersResponse.setOffersList(offerListToOfferListItem(offerList));
        long requestsCount = swapRequestRepository.countUserOfferRequest(label, user);
        long booksCount = bookRepository.countUserAvailableBooks(label, user);
        offersResponse.setAvailableOffersCount(booksCount - requestsCount);
        return offersResponse;
    }

    public OffersResponse filterOffers(OfferFilter offerFilter){
        User user = getCurrentUser();
        List<Book> offerList = bookRepository
                .findBookByStatusAndLabelAndUserIsNot(EBookStatus.AVAILABLE, offerFilter.getLabel(), user)
                .orElse(Collections.emptyList());
        if(!offerFilter.getTitles().isEmpty()){
            offerList = offerList.stream().filter(book -> FilterHelper.containsIgnoreCaseAndTrim(offerFilter.getTitles(),
                            book.getTitle())).collect(Collectors.toList());
        }
        if(!offerFilter.getAuthors().isEmpty()){
            offerList = offerList.stream().filter(book -> FilterHelper.containsIgnoreCaseAndTrim(offerFilter.getAuthors(),
                            book.getAuthor())).collect(Collectors.toList());
        }
        if(!offerFilter.getPublishers().isEmpty()){
            offerList = offerList.stream().filter(book -> FilterHelper.containsIgnoreCaseAndTrim(offerFilter.getPublishers(),
                    book.getPublisher())).collect(Collectors.toList());
        }
        if(offerFilter.getYearOfPublicationTo() != null && !offerFilter.getYearOfPublicationTo().equals("")){
            offerList = offerList.stream()
                    .filter(book -> book.getYearOfPublication() <= Integer.parseInt(offerFilter.getYearOfPublicationTo()))
                    .collect(Collectors.toList());
        }
        if(offerFilter.getYearOfPublicationFrom() != null && !offerFilter.getYearOfPublicationFrom().equals("")){
            offerList = offerList.stream()
                    .filter(book -> book.getYearOfPublication() >= Integer.parseInt(offerFilter.getYearOfPublicationFrom()))
                    .collect(Collectors.toList());
        }
        if(!offerFilter.getCategories().isEmpty()){
            offerList = offerList.stream().filter(book -> FilterHelper.categoriesMatches(offerFilter.getCategories(), book))
                    .collect(Collectors.toList());
        }
        if(!offerFilter.getOwners().isEmpty()){
            offerList = offerList.stream().filter(book -> FilterHelper.containsIgnoreCaseAndTrim(offerFilter.getOwners(),
                    book.getUser().getUsername())).collect(Collectors.toList());
        }
        if(!offerFilter.getLocalization().isEmpty()){
            offerList = offerList.stream().filter(book ->
                    FilterHelper.localizationMatches(offerFilter.getLocalization(), book)).collect(Collectors.toList());
        }
        OffersResponse offersResponse = new OffersResponse();
        offersResponse.setOffersList(offerListToOfferListItem(offerList));
        long requestsCount = swapRequestRepository.countUserOfferRequest(offerFilter.getLabel(), user);
        long booksCount = bookRepository.countUserAvailableBooks(offerFilter.getLabel(), user);
        offersResponse.setAvailableOffersCount(booksCount - requestsCount);
        return offersResponse;
    }

    public FilterHints loadFilterHints(){
        List<Book> bookList = bookRepository
                .findBookByStatusAndUserIsNot(EBookStatus.AVAILABLE, getCurrentUser()).orElse(Collections.emptyList());
        FilterHints filterHints = new FilterHints();
        filterHints.setAuthors(bookList.stream().map(Book::getAuthor).distinct().collect(Collectors.toList()));
        filterHints.setTitles(bookList.stream().map(Book::getTitle).distinct().collect(Collectors.toList()));
        filterHints.setCategories(FilterHelper.getCategoriesFromBooks(bookList));
        filterHints.setPublishers(bookList.stream().map(Book::getPublisher).distinct().collect(Collectors.toList()));
        filterHints.setOwners(bookList.stream().map(book -> book.getUser().getUsername())
                .distinct().collect(Collectors.toList()));
        filterHints.setLocalization(bookList.stream().map(book ->
                book.getUser().getCity() + ", " + book.getUser().getPostalCode() + " " + book.getUser().getPost())
                .distinct().collect(Collectors.toList()));
        return filterHints;
    }

    private User getCurrentUser(){
        UserDetailsI userDetails = (UserDetailsI) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        String username = userDetails.getUsername();
        return userRepository.findByUsername(username).orElseThrow(
                () -> new UsernameNotFoundException("Cannot found user"));
    }

    private List<OfferListItem> offerListToOfferListItem(List<Book> offerList){
        List<String> usersWhoSendOffer = swapRequestRepository.findUsersWhoSendOffers(getCurrentUser())
                .orElse(Collections.emptyList());
        List<OfferListItem> offerListItems = new ArrayList<>();
        for(Book offer: offerList){
            OfferListItem offerListItem = new OfferListItem(offer.getId(), offer.getTitle(),
                    offer.getAuthor());
            if(offer.getImage() != null) {
                offerListItem.setImage(ImageHelper.decompressBytes(offer.getImage()));
            }
            offerListItem.setHasOfferFromUser(usersWhoSendOffer.contains(offer.getUser().getUsername()));
            offerListItems.add(offerListItem);
        }
        return offerListItems;
    }

}
