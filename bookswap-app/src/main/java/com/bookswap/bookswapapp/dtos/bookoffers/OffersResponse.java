package com.bookswap.bookswapapp.dtos.bookoffers;

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
public class OffersResponse implements Serializable {
    private List<OfferListItem> offersList = new ArrayList<>();
    private Long availableOffersCount;
    private Integer totalOffersLength;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OffersResponse that = (OffersResponse) o;
        return Objects.equals(offersList, that.offersList)
                && Objects.equals(availableOffersCount, that.availableOffersCount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(offersList, availableOffersCount);
    }
}
