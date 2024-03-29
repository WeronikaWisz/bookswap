package com.bookswap.bookswapapp.services;

import com.bookswap.bookswapapp.dtos.bookoffers.*;
import com.bookswap.bookswapapp.enums.EBookLabel;
import com.bookswap.bookswapapp.enums.EBookStatus;
import com.bookswap.bookswapapp.enums.ERequestStatus;
import com.bookswap.bookswapapp.enums.ESwapStatus;
import com.bookswap.bookswapapp.exception.ApiExpectationFailedException;
import com.bookswap.bookswapapp.exception.ApiForbiddenException;
import com.bookswap.bookswapapp.exception.ApiNotFoundException;
import com.bookswap.bookswapapp.helpers.FilterHelper;
import com.bookswap.bookswapapp.helpers.ImageHelper;
import com.bookswap.bookswapapp.models.*;
import com.bookswap.bookswapapp.repositories.BookRepository;
import com.bookswap.bookswapapp.repositories.SwapRepository;
import com.bookswap.bookswapapp.repositories.SwapRequestRepository;
import com.bookswap.bookswapapp.repositories.UserRepository;
import com.bookswap.bookswapapp.security.userdetails.UserDetailsI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class BookOffersService {

    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final SwapRequestRepository swapRequestRepository;
    private final SwapRepository swapRepository;
    private static final Logger logger = LoggerFactory.getLogger(BookOffersService.class);

    @Autowired
    public BookOffersService(BookRepository bookRepository, UserRepository userRepository,
                             SwapRequestRepository swapRequestRepository, SwapRepository swapRepository) {
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
        this.swapRequestRepository = swapRequestRepository;
        this.swapRepository = swapRepository;
    }

    public OffersResponse filterOffers(OfferFilter offerFilter, Integer page, Integer size){
        User user = getCurrentUser();
        List<Book> offerList = bookRepository
                .findBookByStatusAndLabelAndUserIsNot(EBookStatus.AVAILABLE, offerFilter.getLabel(), user)
                .orElse(Collections.emptyList());

        offerList = filterListIfRequestAlreadySend(offerList, offerFilter.getLabel(), user);

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
        int total = offerList.size();
        int start = page * size;
        int end = Math.min(start + size, total);
        OffersResponse offersResponse = new OffersResponse();
        if(end >= start) {
            offersResponse.setOffersList(offerListToOfferListItem(offerList.stream()
                    .sorted(Comparator.comparing(Book::getCreationDate).reversed()).collect(Collectors.toList())
                    .subList(start, end)));
        }
        long requestsCount = swapRequestRepository.countUserOfferRequest(offerFilter.getLabel(), user);
        long booksCount = bookRepository.countUserAvailableBooks(offerFilter.getLabel(), user);
        offersResponse.setAvailableOffersCount(booksCount - requestsCount);
        offersResponse.setTotalOffersLength(total);
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

    @Transactional
    public OfferDetails getOffer(Long id){
        Book book = bookRepository.findById(id).orElseThrow(
                () -> new ApiNotFoundException("exception.bookNotFound")
        );
        OfferDetails offerDetails = new OfferDetails(book.getPublisher(), book.getYearOfPublication(), book.getDescription(),
                book.getLabel(), book.getStatus(), book.getUser().getUsername(),
                book.getCategories().stream().map(Category::getName).collect(Collectors.toList()));
        List<SwapRequest> swapRequests = swapRequestRepository.findOffersSendFromUser(
                book.getUser(), getCurrentUser(), book.getLabel()).orElse(Collections.emptyList());
        if(!swapRequests.isEmpty()){
            offerDetails.setHasOfferFromUser(true);
            offerDetails.setRequestedBooks(
                    swapRequests.stream().map(swapRequest -> new RequestBook(swapRequest.getBook().getId(),
                            swapRequest.getBook().getTitle(), swapRequest.getBook().getAuthor()))
                            .collect(Collectors.toList()));
        } else {
            offerDetails.setHasOfferFromUser(false);
        }
        offerDetails.setLocalization(book.getUser().getCity() + ", " +
                book.getUser().getPostalCode() + " " + book.getUser().getPost());
        return offerDetails;
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void sendSwapRequest(BooksForSwap booksForSwap){

        Book book = bookRepository.findById(booksForSwap.getRequestedBookId()).orElseThrow(
                () -> new ApiNotFoundException("exception.requestedBookNotFound")
        );
        if(book.getStatus() != EBookStatus.AVAILABLE){
            throw new ApiExpectationFailedException("exception.requestedBookNotAvailable");
        }
        User user = getCurrentUser();
        long requestsCount = swapRequestRepository.countUserOfferRequest(book.getLabel(), user);
        long booksCount = bookRepository.countUserAvailableBooks(book.getLabel(), user);
        if(booksCount - requestsCount == 0){
            throw new ApiExpectationFailedException("exception.noBookToSwap");
        }
        if(booksForSwap.getUserBookIdForSwap() != null){
            Book bookForSwap = bookRepository.findById(booksForSwap.getUserBookIdForSwap()).orElseThrow(
                    () -> new ApiNotFoundException("exception.swapBookNotFound")
            );
            if(bookForSwap.getStatus() != EBookStatus.AVAILABLE){
                throw new ApiExpectationFailedException("exception.swapBookNotAvailable");
            }

            if(!book.getLabel().equals(bookForSwap.getLabel())){
                throw new ApiExpectationFailedException("exception.labelIncompatible");
            }

            Optional<SwapRequest> existedSwapRequest = swapRequestRepository.findByBookAndUserAndStatus(bookForSwap,
                    book.getUser(), ERequestStatus.WAITING);
            if(existedSwapRequest.isEmpty()){
                throw new ApiExpectationFailedException("exception.requestForUserNotWaiting");
            }
            Swap swap = new Swap();
            swap.setCreationDate(LocalDateTime.now());
            swap.setBook1(book);
            swap.setBook2(bookForSwap);
            swap.setStatus(ESwapStatus.IN_PROGRESS);
            swap.setSwapRequest(existedSwapRequest.get());
            swapRepository.save(swap);

            existedSwapRequest.get().setUpdateDate(LocalDateTime.now());
            existedSwapRequest.get().setStatus(ERequestStatus.ACCEPTED);

            book.setUpdateDate(LocalDateTime.now());
            bookForSwap.setUpdateDate(LocalDateTime.now());

            if(book.getLabel().equals(EBookLabel.PERMANENT_SWAP)){
                book.setStatus(EBookStatus.PERMANENT_SWAP);
                bookForSwap.setStatus(EBookStatus.PERMANENT_SWAP);
            } else {
                book.setStatus(EBookStatus.TEMPORARY_SWAP);
                bookForSwap.setStatus(EBookStatus.TEMPORARY_SWAP);
            }

            List<Book> swapBooks = new ArrayList<>();
            swapBooks.add(book);
            swapBooks.add(bookForSwap);

            this.deniedWaitingRequestForBooks(swapBooks);

        } else {
            Optional<SwapRequest> existedSwapRequest = swapRequestRepository
                    .findByBookAndUserAndStatus(book, user, ERequestStatus.WAITING);
            if(existedSwapRequest.isPresent()){
                throw new ApiExpectationFailedException("exception.requestAlreadySend");
            }
            SwapRequest swapRequest = new SwapRequest();
            swapRequest.setCreationDate(LocalDateTime.now());
            swapRequest.setBook(book);
            swapRequest.setUser(user);
            swapRequest.setStatus(ERequestStatus.WAITING);

            swapRequestRepository.save(swapRequest);
        }
    }

    @Transactional
    public RequestsResponse getSentRequests(SwapRequestFilter swapRequestFilter, Integer page, Integer size){
        List<SwapRequest> swapRequests = swapRequestRepository.findByUserAndStatusIn(
                getCurrentUser(), swapRequestFilter.getRequestStatus()
        ).orElse(Collections.emptyList());
        if(swapRequestFilter.getBookLabel() != null){
            swapRequests = swapRequests.stream().filter(
                    swapRequest -> swapRequest.getBook().getLabel().equals(swapRequestFilter.getBookLabel()))
                    .collect(Collectors.toList());
        }
        return getRequestsResponse(swapRequests, page, size);
    }

    @Transactional
    public RequestsResponse getReceivedRequests(SwapRequestFilter swapRequestFilter, Integer page, Integer size){
        List<SwapRequest> swapRequests = swapRequestRepository.findByBook_UserAndStatusIn(
                getCurrentUser(), swapRequestFilter.getRequestStatus()
        ).orElse(Collections.emptyList());
        if(swapRequestFilter.getBookLabel() != null){
            swapRequests = swapRequests.stream().filter(
                            swapRequest -> swapRequest.getBook().getLabel().equals(swapRequestFilter.getBookLabel()))
                    .collect(Collectors.toList());
        }
        return getRequestsResponse(swapRequests, page, size);
    }

    private RequestsResponse getRequestsResponse(List<SwapRequest> swapRequests, Integer page, Integer size){
        int total = swapRequests.size();
        int start = page * size;
        int end = Math.min(start + size, total);
        RequestsResponse requestsResponse = new RequestsResponse();
        if(end >= start){
            requestsResponse.setRequestsList(getRequestListItems(swapRequests.stream()
                    .sorted(Comparator.comparing(SwapRequest::getCreationDate).reversed()).collect(Collectors.toList()))
                    .subList(start, end));
        }
        requestsResponse.setTotalRequestsLength(total);
        return requestsResponse;
    }

    @Transactional
    public void cancelSwapRequest(Long id){
        SwapRequest swapRequest = getWaitingSwapRequest(id);
        if(swapRequest.getUser() != getCurrentUser()){
            throw new ApiForbiddenException("exception.cannotCancel");
        }
        swapRequest.setUpdateDate(LocalDateTime.now());
        swapRequest.setStatus(ERequestStatus.CANCELED);
    }

    @Transactional
    public void denySwapRequest(Long id){
        SwapRequest swapRequest = getWaitingSwapRequest(id);
        if(swapRequest.getBook().getUser() != getCurrentUser()){
            throw new ApiForbiddenException("exception.cannotDeny");
        }
        swapRequest.setUpdateDate(LocalDateTime.now());
        swapRequest.setStatus(ERequestStatus.DENIED);
    }

    private SwapRequest getWaitingSwapRequest(Long id){
        SwapRequest swapRequest = swapRequestRepository.findById(id)
                .orElseThrow(() ->
                        new ApiNotFoundException("exception.requestNotExists"));
        if(swapRequest.getStatus() != ERequestStatus.WAITING){
            throw new ApiExpectationFailedException("exception.requestNotWaiting");
        }
        return swapRequest;
    }

    private User getCurrentUser(){
        UserDetailsI userDetails = (UserDetailsI) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        String username = userDetails.getUsername();
        return userRepository.findByUsername(username).orElseThrow(
                () -> new UsernameNotFoundException("Cannot found user"));
    }

    private List<OfferListItem> offerListToOfferListItem(List<Book> offerList) {
        List<OfferListItem> offerListItems = new ArrayList<>();
        for (Book offer : offerList) {
            OfferListItem offerListItem = new OfferListItem(offer.getId(), offer.getTitle(),
                    offer.getAuthor());
            if (offer.getImage() != null) {
                offerListItem.setImage(ImageHelper.decompressBytes(offer.getImage()));
            }
            offerListItems.add(offerListItem);
        }
        return offerListItems;
    }

    private List<Book> filterListIfRequestAlreadySend(List<Book> offerList, EBookLabel label, User user){
        Optional<List<Long>> userSendRequestsBooks = swapRequestRepository.userSendRequestsBooks(label, user);

        if(userSendRequestsBooks.isPresent() && !userSendRequestsBooks.get().isEmpty()){
            offerList = offerList.stream()
                    .filter(book -> !userSendRequestsBooks.get().contains(book.getId())).collect(Collectors.toList());
        }

        return offerList;
    }

    private void deniedWaitingRequestForBooks(List<Book> swapBooks){
        Optional<List<SwapRequest>> swapRequests = swapRequestRepository.findByBookInAndStatusAndBook_Label(swapBooks,
                ERequestStatus.WAITING, EBookLabel.PERMANENT_SWAP);
        if(swapRequests.isPresent() && !swapRequests.get().isEmpty()){
            swapRequests.get().forEach(swapRequest -> swapRequest.setStatus(ERequestStatus.DENIED));
        }
    }

    private List<SwapRequestListItem> getRequestListItems(List<SwapRequest> swapRequests){
        List<SwapRequestListItem> swapRequestListItems = new ArrayList<>();
        for(SwapRequest swapRequest: swapRequests){
            SwapRequestListItem swapRequestListItem = new SwapRequestListItem(swapRequest.getId(),
                    swapRequest.getBook().getTitle(), swapRequest.getBook().getAuthor(),
                    swapRequest.getBook().getLabel(), swapRequest.getBook().getStatus(),
                    swapRequest.getUser().getUsername(), swapRequest.getBook().getUser().getUsername(),
                    swapRequest.getStatus());
            if (swapRequest.getBook().getImage() != null) {
                swapRequestListItem.setBookImage(ImageHelper.decompressBytes(swapRequest.getBook().getImage()));
            }
            swapRequestListItems.add(swapRequestListItem);
        }
        return swapRequestListItems;
    }
}
