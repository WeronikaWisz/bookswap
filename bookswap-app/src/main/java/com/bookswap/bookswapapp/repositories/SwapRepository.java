package com.bookswap.bookswapapp.repositories;

import com.bookswap.bookswapapp.models.Swap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SwapRepository extends JpaRepository<Swap, Long>  {
}
