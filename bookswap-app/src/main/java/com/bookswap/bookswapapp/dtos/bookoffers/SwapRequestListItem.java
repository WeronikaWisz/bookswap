package com.bookswap.bookswapapp.dtos.bookoffers;

import com.bookswap.bookswapapp.enums.EBookLabel;
import com.bookswap.bookswapapp.enums.EBookStatus;
import com.bookswap.bookswapapp.enums.ERequestStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SwapRequestListItem {
    private Long id;
    private String bookTitle;
    private String bookAuthor;
    private EBookLabel bookLabel;
    private EBookStatus bookStatus;
    private byte[] bookImage;
    private String sender;
    private String owner;
    private ERequestStatus requestStatus;

    public SwapRequestListItem(Long id, String bookTitle, String bookAuthor, EBookLabel bookLabel,
                               EBookStatus bookStatus, String sender, String owner, ERequestStatus requestStatus) {
        this.id = id;
        this.bookTitle = bookTitle;
        this.bookAuthor = bookAuthor;
        this.bookLabel = bookLabel;
        this.bookStatus = bookStatus;
        this.sender = sender;
        this.owner = owner;
        this.requestStatus = requestStatus;
    }
}
