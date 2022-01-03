package com.bookswap.bookswapapp.dtos.bookoffers;

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
public class RequestsResponse implements Serializable {
    private List<SwapRequestListItem> requestsList = new ArrayList<>();
    private Integer totalRequestsLength;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RequestsResponse that = (RequestsResponse) o;
        return Objects.equals(requestsList, that.requestsList)
                && Objects.equals(totalRequestsLength, that.totalRequestsLength);
    }

    @Override
    public int hashCode() {
        return Objects.hash(requestsList, totalRequestsLength);
    }
}
