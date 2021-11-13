package com.bookswap.bookswapapp.dtos.userbooks;

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
public class BookDetails {
    private String publisher;
    private Integer yearOfPublication;
    private String description;
    private EBookStatus status;
    private EBookLabel label;
    private List<String> categories = new ArrayList<>();
}
