package com.gustavo.sfrestlibrary.services;

import com.gustavo.sfrestlibrary.model.BookDTO;
import com.gustavo.sfrestlibrary.model.BookGenre;
import org.springframework.data.domain.Page;

import java.util.Optional;
import java.util.UUID;

public interface BookService {
    BookDTO createBook(BookDTO book);
    Page<BookDTO> listAllBooks(Long bookIsbn, String bookName, BookGenre bookGenre, Integer bookVersion,
                               Integer pageNumber, Integer pageSize);
    Optional<BookDTO> getBookById(UUID bookId);
    Optional<BookDTO> updateBookById(UUID bookId, BookDTO book);
    Optional<BookDTO> patchBookById(UUID bookId, BookDTO book);
    Boolean deleteBookById(UUID bookId);
}
