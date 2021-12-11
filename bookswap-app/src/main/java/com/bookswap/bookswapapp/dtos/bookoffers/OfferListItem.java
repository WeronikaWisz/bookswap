package com.bookswap.bookswapapp.dtos.bookoffers;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
public class OfferListItem implements Serializable {
    private Long id;
    private String title;
    private String author;
    private byte[] image;

    public OfferListItem(Long id, String title, String author) {
        this.id = id;
        this.title = title;
        this.author = author;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OfferListItem that = (OfferListItem) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
