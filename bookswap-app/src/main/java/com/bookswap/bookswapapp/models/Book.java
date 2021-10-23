package com.bookswap.bookswapapp.models;

import com.bookswap.bookswapapp.enums.EBookLabel;
import com.bookswap.bookswapapp.enums.EBookStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table
@Getter
@Setter
@NoArgsConstructor
public class Book {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;
    private String title;
    private String author;
    private String publisher;
    private Integer yearOfPublication;
    private String category;
    private String description;
    @Lob
    private byte[] image;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable=false)
    private User user;
    @Enumerated
    @Column(columnDefinition = "smallint")
    private EBookStatus status;
    @Enumerated
    @Column(columnDefinition = "smallint")
    private EBookLabel label;
    private LocalDateTime creationDate;
    private LocalDateTime updateDate;

}
