package com.gustavo.sfrestlibrary.model;

import java.time.LocalDateTime;
import java.util.UUID;

public record BookDTO(UUID bookId, int bookIsbn, String bookName, BookGenre bookgenre, Integer bookVersion,
                      LocalDateTime bookCreationDate, LocalDateTime bookUpdateDate) {
}
