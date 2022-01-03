package com.bookswap.bookswapapp.dtos.bookoffers;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
public class BooksForSwap implements Serializable {
    private Long requestedBookId;
    private Long userBookIdForSwap;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BooksForSwap that = (BooksForSwap) o;
        return Objects.equals(requestedBookId, that.requestedBookId)
                && Objects.equals(userBookIdForSwap, that.userBookIdForSwap);
    }

    @Override
    public int hashCode() {
        return Objects.hash(requestedBookId, userBookIdForSwap);
    }
}
