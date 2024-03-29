package com.bookswap.bookswapapp.models;

import com.bookswap.bookswapapp.enums.ESwapStatus;
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
public class Swap {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book1_id", nullable=false)
    private Book book1;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book2_id", nullable=false)
    private Book book2;
    @Enumerated
    @Column(columnDefinition = "smallint")
    private ESwapStatus status;
    private LocalDateTime creationDate;
    private LocalDateTime updateDate;
    @OneToOne
    @JoinColumn(name="swap_request_id")
    private SwapRequest swapRequest;
}
