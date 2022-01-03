package com.bookswap.bookswapapp.dtos.userbooks;

import com.bookswap.bookswapapp.enums.EBookLabel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Setter
@Getter
@NoArgsConstructor
public class BookListItem implements Serializable {
    private Long id;
    private String title;
    private String author;
    private byte[] image;
    private EBookLabel label;

    public BookListItem(Long id, String title, String author, EBookLabel label) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.label = label;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BookListItem that = (BookListItem) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
