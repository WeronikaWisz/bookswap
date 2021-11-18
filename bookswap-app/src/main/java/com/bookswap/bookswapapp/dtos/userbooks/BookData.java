package com.bookswap.bookswapapp.dtos.userbooks;

import com.bookswap.bookswapapp.enums.EBookLabel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class BookData {
    private String title;
    private String author;
    private String publisher;
    private Integer yearOfPublication;
    private String description;
    private List<String> categories = new ArrayList<>();
    private EBookLabel label;
}
