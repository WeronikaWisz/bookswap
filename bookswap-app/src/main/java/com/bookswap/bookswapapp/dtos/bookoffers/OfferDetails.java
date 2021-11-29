package com.bookswap.bookswapapp.dtos.bookoffers;

import com.bookswap.bookswapapp.enums.EBookLabel;
import com.bookswap.bookswapapp.enums.EBookStatus;
import com.fasterxml.jackson.annotation.JsonProperty;
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
    private EBookStatus status;
    private String owner;
    private String localization;
    private List<String> categories = new ArrayList<>();
    @JsonProperty
    private boolean hasOfferFromUser;
    private List<RequestBook> requestedBooks = new ArrayList<>();

    public OfferDetails(String publisher, Integer yearOfPublication, String description, EBookLabel label,
                        EBookStatus status, String owner, List<String> categories) {
        this.publisher = publisher;
        this.yearOfPublication = yearOfPublication;
        this.description = description;
        this.label = label;
        this.status = status;
        this.owner = owner;
        this.categories = categories;
    }
}
