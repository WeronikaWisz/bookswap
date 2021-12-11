package com.bookswap.bookswapapp.dtos.bookswaps;

import com.bookswap.bookswapapp.enums.EBookLabel;
import com.bookswap.bookswapapp.enums.ESwapStatus;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
public class SwapListItem implements Serializable {
    private Long id;
    private String currentUserBookTitle;
    private String currentUserBookAuthor;
    private EBookLabel currentUserBookLabel;
    private byte[] currentUserBookImage;
    private String otherUserBookTitle;
    private String otherUserBookAuthor;
    private EBookLabel otherUserBookLabel;
    private byte[] otherUserBookImage;
    private String otherUsername;
    private ESwapStatus swapStatus;
    @JsonProperty
    private boolean ifCurrentUserConfirmed;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SwapListItem that = (SwapListItem) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
