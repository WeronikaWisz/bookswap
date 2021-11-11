package com.bookswap.bookswapapp.dtos.userbooks;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class NewBook {
    private String title;
    private String author;
    private String publisher;
    private Integer yearOfPublication;
    private String description;
    private List<String> categories;
    private String label;
}
