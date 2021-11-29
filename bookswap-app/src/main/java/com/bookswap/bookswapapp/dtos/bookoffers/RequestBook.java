package com.bookswap.bookswapapp.dtos.bookoffers;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RequestBook {
    private Long id;
    private String title;
    private String author;

    public RequestBook(Long id, String title, String author) {
        this.id = id;
        this.title = title;
        this.author = author;
    }
}
