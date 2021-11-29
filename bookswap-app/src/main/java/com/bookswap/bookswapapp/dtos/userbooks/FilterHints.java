package com.bookswap.bookswapapp.dtos.userbooks;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class FilterHints {
    private List<String> titles = new ArrayList<>();
    private List<String> authors = new ArrayList<>();
    private List<String> publishers = new ArrayList<>();
    private List<String> categories = new ArrayList<>();
}
