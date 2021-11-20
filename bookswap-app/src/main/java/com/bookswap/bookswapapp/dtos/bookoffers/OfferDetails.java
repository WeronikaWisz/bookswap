package com.bookswap.bookswapapp.dtos.bookoffers;

import com.bookswap.bookswapapp.enums.EBookLabel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class OfferDetails {
    private String publisher;
    private Integer yearOfPublication;
    private String description;
    private EBookLabel label;
    private String owner;
    private List<String> categories = new ArrayList<>();
}
