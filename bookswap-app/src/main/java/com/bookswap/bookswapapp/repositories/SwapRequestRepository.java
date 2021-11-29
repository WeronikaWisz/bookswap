package com.bookswap.bookswapapp.repositories;

import com.bookswap.bookswapapp.enums.EBookLabel;
import com.bookswap.bookswapapp.enums.ERequestStatus;
import com.bookswap.bookswapapp.models.Book;
import com.bookswap.bookswapapp.models.SwapRequest;
import com.bookswap.bookswapapp.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SwapRequestRepository extends JpaRepository<SwapRequest, Long> {

    @Query("select count(sr) from SwapRequest sr where sr.status = 0 and sr.book.label = ?1 and sr.user = ?2")
    long countUserOfferRequest(EBookLabel label, User user);

    @Query("select sr from SwapRequest sr where sr.user = ?1 and sr.book.user = ?2 and sr.status = 0 " +
            "and sr.book.status = 0 and sr.book.label = ?3")
    Optional<List<SwapRequest>> findOffersSendFromUser(User userFrom, User userTo, EBookLabel label);

    Optional<SwapRequest> findByBookAndUserAndStatus(Book book, User user, ERequestStatus status);

    @Query("select sr.book.id from SwapRequest sr where sr.status = 0 and sr.book.label = ?1 and sr.user = ?2")
    Optional<List<Long>> userSendRequestsBooks(EBookLabel label, User user);

    Optional<List<SwapRequest>> findByBookInAndStatusAndBook_Label(List<Book> books, ERequestStatus status, EBookLabel label);

    Optional<List<SwapRequest>> findByUserAndStatusIn(User user, List<ERequestStatus> statuses);

    Optional<List<SwapRequest>> findByBook_UserAndStatusIn(User user, List<ERequestStatus> statuses);

}
