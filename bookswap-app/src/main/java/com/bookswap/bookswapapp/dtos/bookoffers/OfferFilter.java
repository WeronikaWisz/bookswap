package com.bookswap.bookswapapp.dtos.bookoffers;

import com.bookswap.bookswapapp.enums.EBookLabel;
import com.bookswap.bookswapapp.enums.EBookStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class OfferFilter {
    private List<String> titles = new ArrayList<>();
    private List<String> authors = new ArrayList<>();
    private List<String> publishers = new ArrayList<>();
    private String yearOfPublicationFrom;
    private String yearOfPublicationTo;
    private List<String> categories = new ArrayList<>();
    private List<String> localization = new ArrayList<>();
    private EBookLabel label;
    private List<String> owners = new ArrayList<>();
}
