package com.bookswap.bookswapapp.dtos.bookoffers;

import com.bookswap.bookswapapp.enums.EBookLabel;
import com.bookswap.bookswapapp.enums.EBookStatus;
import com.fasterxml.jackson.annotation.JsonProperty;
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
public class OfferDetails implements Serializable {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OfferDetails that = (OfferDetails) o;
        return hasOfferFromUser == that.hasOfferFromUser && Objects.equals(publisher, that.publisher)
                && Objects.equals(yearOfPublication, that.yearOfPublication)
                && Objects.equals(description, that.description) && label == that.label && status == that.status
                && Objects.equals(owner, that.owner) && Objects.equals(localization, that.localization)
                && Objects.equals(categories, that.categories) && Objects.equals(requestedBooks, that.requestedBooks);
    }

    @Override
    public int hashCode() {
        return Objects.hash(publisher, yearOfPublication, description, label, status,
                owner, localization, categories, hasOfferFromUser, requestedBooks);
    }
}
