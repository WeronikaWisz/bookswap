package com.bookswap.bookswapapp.dtos.userbooks;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class BookListItem {
    private String title;
    private String author;
    private byte[] image;

    public BookListItem(String title, String author) {
        this.title = title;
        this.author = author;
    }
}
