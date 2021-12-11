package com.bookswap.bookswapapp.dtos.bookswaps;

import com.bookswap.bookswapapp.enums.EBookLabel;
import com.bookswap.bookswapapp.enums.ESwapStatus;
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
public class SwapFilter implements Serializable {
    private List<ESwapStatus> swapStatus = new ArrayList<>();
    private EBookLabel bookLabel;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SwapFilter that = (SwapFilter) o;
        return Objects.equals(swapStatus, that.swapStatus) && bookLabel == that.bookLabel;
    }

    @Override
    public int hashCode() {
        return Objects.hash(swapStatus, bookLabel);
    }
}
