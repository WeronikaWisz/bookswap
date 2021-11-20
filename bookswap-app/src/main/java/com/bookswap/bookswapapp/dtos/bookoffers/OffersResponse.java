package com.bookswap.bookswapapp.dtos.bookoffers;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class OffersResponse {
    private List<OfferListItem> offersList = new ArrayList<>();
    private Long availableOffersCount;
}
