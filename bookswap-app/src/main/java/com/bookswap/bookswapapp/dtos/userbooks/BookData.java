package com.bookswap.bookswapapp.dtos.userbooks;

import com.bookswap.bookswapapp.enums.EBookLabel;
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
public class BookData implements Serializable {
    private String title;
    private String author;
    private String publisher;
    private Integer yearOfPublication;
    private String description;
    private List<String> categories = new ArrayList<>();
    private EBookLabel label;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BookData bookData = (BookData) o;
        return Objects.equals(title, bookData.title) && Objects.equals(author, bookData.author)
                && Objects.equals(publisher, bookData.publisher)
                && Objects.equals(yearOfPublication, bookData.yearOfPublication)
                && Objects.equals(description, bookData.description)
                && Objects.equals(categories, bookData.categories) && label == bookData.label;
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, author, publisher, yearOfPublication, description, categories, label);
    }
}
