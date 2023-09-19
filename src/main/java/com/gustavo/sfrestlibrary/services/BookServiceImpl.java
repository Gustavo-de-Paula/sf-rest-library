package com.gustavo.sfrestlibrary.services;

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

    public BookServiceImpl() {
        this.bookMap = new HashMap<>();

        BookDTO book1 = BookDTO.builder()
                .bookId(UUID.randomUUID())
                .bookIsbn(1111111111111L)
                .bookName("Neuromancer")
                .bookGenre(BookGenre.SCIFI)
                .bookVersion(1)
                .bookCreationDate(LocalDateTime.now())
                .bookUpdateDate(LocalDateTime.now())
                .build();

        BookDTO book2 = BookDTO.builder()
                .bookId(UUID.randomUUID())
                .bookIsbn(2222222222222L)
                .bookName("The Color Out of Space")
                .bookGenre(BookGenre.HORROR)
                .bookVersion(2)
                .bookCreationDate(LocalDateTime.now())
                .bookUpdateDate(LocalDateTime.now())
                .build();

        BookDTO book3 = BookDTO.builder()
                .bookId(UUID.randomUUID())
                .bookIsbn(3333333333333L)
                .bookName("Lord of the Rings: The Two Towers")
                .bookGenre(BookGenre.FANTASY)
                .bookVersion(3)
                .bookCreationDate(LocalDateTime.now())
                .bookUpdateDate(LocalDateTime.now())
                .build();

        bookMap.put(book1.getBookId(), book1);
        bookMap.put(book2.getBookId(), book2);
        bookMap.put(book3.getBookId(), book3);
    }

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
    public Page<BookDTO> listAllBooks(Long bookIsbn, String bookName, BookGenre bookgenre, Integer bookVersion,
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
