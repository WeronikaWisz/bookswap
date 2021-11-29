package com.bookswap.bookswapapp.dtos.bookswaps;

import com.bookswap.bookswapapp.enums.EBookLabel;
import com.bookswap.bookswapapp.enums.ESwapStatus;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SwapListItem {
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
}
