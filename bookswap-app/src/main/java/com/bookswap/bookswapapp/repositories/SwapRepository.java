package com.bookswap.bookswapapp.repositories;

import com.bookswap.bookswapapp.enums.EBookLabel;
import com.bookswap.bookswapapp.enums.ESwapStatus;
import com.bookswap.bookswapapp.models.Swap;
import com.bookswap.bookswapapp.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SwapRepository extends JpaRepository<Swap, Long>  {

    @Query("select s from Swap s where s.status in ?1 and s.book1.label = ?2 and s.book2.label = ?2 and " +
            "(s.book1.user = ?3 or s.book2.user = ?3)")
    Optional<List<Swap>> findUserSwaps(List<ESwapStatus> statuses, EBookLabel label, User user);
}
