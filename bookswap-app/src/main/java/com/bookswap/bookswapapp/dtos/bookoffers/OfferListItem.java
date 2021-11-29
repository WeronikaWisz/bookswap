package com.bookswap.bookswapapp.dtos.bookoffers;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class OfferListItem {
    private Long id;
    private String title;
    private String author;
    private byte[] image;

    public OfferListItem(Long id, String title, String author) {
        this.id = id;
        this.title = title;
        this.author = author;
    }
}
