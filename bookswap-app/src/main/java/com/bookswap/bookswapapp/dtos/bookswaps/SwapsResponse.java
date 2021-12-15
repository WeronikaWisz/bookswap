package com.bookswap.bookswapapp.dtos.bookswaps;

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
public class SwapsResponse implements Serializable {
    private List<SwapListItem> swapsList = new ArrayList<>();
    private Integer totalSwapsLength;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SwapsResponse that = (SwapsResponse) o;
        return Objects.equals(swapsList, that.swapsList) && Objects.equals(totalSwapsLength, that.totalSwapsLength);
    }

    @Override
    public int hashCode() {
        return Objects.hash(swapsList, totalSwapsLength);
    }
}
