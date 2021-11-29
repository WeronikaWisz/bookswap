package com.bookswap.bookswapapp.dtos.userbooks;

import com.bookswap.bookswapapp.enums.EBookLabel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class BookListItem {
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
}
