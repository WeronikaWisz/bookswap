package com.bookswap.bookswapapp.repositories;

import com.bookswap.bookswapapp.enums.EBookLabel;
import com.bookswap.bookswapapp.enums.EBookStatus;
import com.bookswap.bookswapapp.models.Book;
import com.bookswap.bookswapapp.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    @Transactional
    Optional<List<Book>> findBookByStatusAndUser(EBookStatus status, User user);

    @Transactional
    Optional<List<Book>> findBookByUser(User user);

    @Transactional
    Optional<List<Book>> findBookByLabelAndUser(EBookLabel label, User user);

    @Transactional
    Optional<List<Book>> findBookByStatusAndLabelAndUser(EBookStatus status, EBookLabel label, User user);

    @Transactional
    Optional<List<Book>> findBookByStatusAndUserIsNot(EBookStatus status, User user);

    @Transactional
    Optional<List<Book>> findBookByStatusAndLabelAndUserIsNot(EBookStatus status, EBookLabel label, User user);

    @Query("select count(b) from Book b where b.status = 0 and b.label = ?1 and b.user = ?2")
    long countUserAvailableBooks(EBookLabel label, User user);
}
