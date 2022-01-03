package com.bookswap.bookswapapp.dtos.userbooks;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
public class BooksResponse implements Serializable {
    private List<BookListItem> booksList = new ArrayList<>();
    private Integer totalBooksLength;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BooksResponse that = (BooksResponse) o;
        return Objects.equals(booksList, that.booksList) && Objects.equals(totalBooksLength, that.totalBooksLength);
    }

    @Override
    public int hashCode() {
        return Objects.hash(booksList, totalBooksLength);
    }
}
