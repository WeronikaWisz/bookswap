package com.bookswap.bookswapapp.dtos.userbooks;

import com.bookswap.bookswapapp.enums.EBookLabel;
import com.bookswap.bookswapapp.enums.EBookStatus;
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
public class BookFilter implements Serializable {
    private List<String> titles = new ArrayList<>();
    private List<String> authors = new ArrayList<>();
    private List<String> publishers = new ArrayList<>();
    private String yearOfPublicationFrom;
    private String yearOfPublicationTo;
    private List<String> categories = new ArrayList<>();
    private EBookStatus status;
    private EBookLabel label;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BookFilter that = (BookFilter) o;
        return Objects.equals(titles, that.titles) && Objects.equals(authors, that.authors)
                && Objects.equals(publishers, that.publishers)
                && Objects.equals(yearOfPublicationFrom, that.yearOfPublicationFrom)
                && Objects.equals(yearOfPublicationTo, that.yearOfPublicationTo)
                && Objects.equals(categories, that.categories) && status == that.status && label == that.label;
    }

    @Override
    public int hashCode() {
        return Objects.hash(titles, authors, publishers, yearOfPublicationFrom, yearOfPublicationTo, categories, status, label);
    }
}
