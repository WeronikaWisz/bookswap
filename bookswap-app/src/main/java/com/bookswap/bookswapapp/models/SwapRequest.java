package com.bookswap.bookswapapp.models;

import com.bookswap.bookswapapp.enums.ERequestStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table
@Getter
@Setter
@NoArgsConstructor
public class SwapRequest {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable=false)
    private User user;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable=false)
    private Book book;
    @Enumerated
    @Column(columnDefinition = "smallint")
    private ERequestStatus status;
    private LocalDateTime creationDate;
    private LocalDateTime updateDate;
}
