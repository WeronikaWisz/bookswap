package com.bookswap.bookswapapp.repositories;

import com.bookswap.bookswapapp.enums.EBookLabel;
import com.bookswap.bookswapapp.models.SwapRequest;
import com.bookswap.bookswapapp.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SwapRequestRepository extends JpaRepository<SwapRequest, Long> {

    @Query("select sr.user.username from SwapRequest sr where sr.book.user = ?1")
    Optional<List<String>> findUsersWhoSendOffers(User user);

    @Query("select count(sr) from SwapRequest sr where sr.status = 0 and sr.book.label = ?1 and sr.user = ?2")
    long countUserOfferRequest(EBookLabel label, User user);

}
