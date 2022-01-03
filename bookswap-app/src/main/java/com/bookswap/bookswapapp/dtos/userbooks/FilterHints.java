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
public class FilterHints implements Serializable {
    private List<String> titles = new ArrayList<>();
    private List<String> authors = new ArrayList<>();
    private List<String> publishers = new ArrayList<>();
    private List<String> categories = new ArrayList<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FilterHints that = (FilterHints) o;
        return Objects.equals(titles, that.titles) && Objects.equals(authors, that.authors) && Objects.equals(publishers, that.publishers) && Objects.equals(categories, that.categories);
    }

    @Override
    public int hashCode() {
        return Objects.hash(titles, authors, publishers, categories);
    }
}
