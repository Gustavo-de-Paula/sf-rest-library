package com.gustavo.sfrestlibrary.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class BookDTO {
    private UUID bookId;
    private int bookIsbn;
    private String bookName;
    private BookGenre bookGenre;
    private Integer bookVersion;
    private LocalDateTime bookCreationDate;
    private LocalDateTime bookUpdateDate;
}
