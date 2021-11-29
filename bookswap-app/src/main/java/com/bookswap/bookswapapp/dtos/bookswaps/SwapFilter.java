package com.bookswap.bookswapapp.dtos.bookswaps;

import com.bookswap.bookswapapp.enums.EBookLabel;
import com.bookswap.bookswapapp.enums.ESwapStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class SwapFilter {
    private List<ESwapStatus> swapStatus = new ArrayList<>();
    private EBookLabel bookLabel;
}
