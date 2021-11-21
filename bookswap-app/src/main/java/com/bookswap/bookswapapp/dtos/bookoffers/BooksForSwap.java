package com.bookswap.bookswapapp.dtos.bookoffers;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class BooksForSwap {
    private Long requestedBookId;
    private Long userBookIdForSwap;
}
