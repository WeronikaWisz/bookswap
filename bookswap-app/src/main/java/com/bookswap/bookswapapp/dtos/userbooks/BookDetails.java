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
public class BookDetails implements Serializable {
    private String publisher;
    private Integer yearOfPublication;
    private String description;
    private EBookStatus status;
    private EBookLabel label;
    private List<String> categories = new ArrayList<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BookDetails that = (BookDetails) o;
        return Objects.equals(publisher, that.publisher) && Objects.equals(yearOfPublication, that.yearOfPublication)
                && Objects.equals(description, that.description) && status == that.status && label == that.label
                && Objects.equals(categories, that.categories);
    }

    @Override
    public int hashCode() {
        return Objects.hash(publisher, yearOfPublication, description, status, label, categories);
    }
}
