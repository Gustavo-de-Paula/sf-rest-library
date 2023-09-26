package com.gustavo.sfrestlibrary.services;

import com.gustavo.sfrestlibrary.entities.Book;
import com.gustavo.sfrestlibrary.model.BookDTO;
import com.gustavo.sfrestlibrary.model.BookGenre;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
public class BookServiceImpl implements BookService {
    private Map<UUID, BookDTO> bookMap;

    @Override
    public BookDTO createBook(BookDTO book) {
        BookDTO savedBook = BookDTO.builder()
                .bookId(UUID.randomUUID())
                .bookIsbn(book.getBookIsbn())
                .bookName(book.getBookName())
                .bookGenre(book.getBookGenre())
                .bookVersion(book.getBookVersion())
                .bookCreationDate(LocalDateTime.now())
                .bookUpdateDate(LocalDateTime.now())
                .build();

        bookMap.put(savedBook.getBookId(), book);

        return savedBook;
    }

    @Override
    public Page<BookDTO> listAllBooks(Long bookIsbn, String bookName, BookGenre bookgenre,
                                      Integer pageNumber, Integer pageSize) {
        return new PageImpl<>(new ArrayList<>(bookMap.values()));
    }

    @Override
    public Optional<BookDTO> getBookById(UUID bookId) {
        return Optional.of(bookMap.get(bookId));
    }

    @Override
    public Optional<BookDTO> updateBookById(UUID bookId, BookDTO book) {
        BookDTO existing = bookMap.get(bookId);

        existing.setBookIsbn(book.getBookIsbn());
        existing.setBookName(book.getBookName());
        existing.setBookGenre(book.getBookGenre());
        existing.setBookVersion(book.getBookVersion());
        existing.setBookUpdateDate(LocalDateTime.now());

        return Optional.of(existing);
    }

    @Override
    public Optional<BookDTO> patchBookById(UUID bookId, BookDTO book) {
        BookDTO existing = bookMap.get(bookId);

        if (book.getBookIsbn() != null)
            existing.setBookIsbn(book.getBookIsbn());
        if (StringUtils.hasText(book.getBookName()))
            existing.setBookName(book.getBookName());
        if (book.getBookGenre() != null)
            existing.setBookGenre(book.getBookGenre());
        if (book.getBookVersion() != null)
            existing.setBookVersion(book.getBookVersion());
        existing.setBookUpdateDate(LocalDateTime.now());

        return Optional.of(existing);
    }

    @Override
    public Boolean deleteBookById(UUID bookId) {
        bookMap.remove(bookId);
        return true;
    }
}
