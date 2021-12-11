package com.bookswap.bookswapapp.dtos.bookoffers;

import com.bookswap.bookswapapp.enums.EBookLabel;
import com.bookswap.bookswapapp.enums.ERequestStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
public class SwapRequestFilter implements Serializable {
    private List<ERequestStatus> requestStatus;
    private EBookLabel bookLabel;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SwapRequestFilter that = (SwapRequestFilter) o;
        return Objects.equals(requestStatus, that.requestStatus) && bookLabel == that.bookLabel;
    }

    @Override
    public int hashCode() {
        return Objects.hash(requestStatus, bookLabel);
    }
}
