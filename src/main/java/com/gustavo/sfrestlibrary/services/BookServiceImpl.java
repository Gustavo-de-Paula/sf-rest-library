package com.gustavo.sfrestlibrary.services;

import com.gustavo.sfrestlibrary.entities.Book;
import com.gustavo.sfrestlibrary.mappers.BookMapper;
import com.gustavo.sfrestlibrary.model.BookDTO;
import com.gustavo.sfrestlibrary.model.BookGenre;
import com.gustavo.sfrestlibrary.repositories.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService{
    private final BookRepository bookRepository;
    private final BookMapper bookMapper;

    private static final int DEFAULT_PAGE = 0;
    private static final int DEFAULT_PAGE_SIZE = 25;

    @Override
    public BookDTO createBook(BookDTO book) {
        return bookMapper.bookToBookDto(bookRepository.save(bookMapper.bookDtoToBook(book)));
    }

    @Override
    public Page<BookDTO> listAllBooks(Long bookIsbn, String bookName, BookGenre bookGenre,
                                      Integer pageNumber, Integer pageSize) {
        Page<Book> bookPage;
        PageRequest pageRequest = buildPageRequest(pageNumber, pageSize);

        if (StringUtils.hasText(bookName) && bookGenre == null) {
            bookPage = listAllBooksByName(bookName, pageRequest);
        } else if (!StringUtils.hasText(bookName) && bookGenre != null) {
            bookPage = listAllBooksByGenre(bookGenre, pageRequest);
        } else if (StringUtils.hasText(bookName) && bookGenre != null) {
            bookPage = listAllBooksByNameAndGenre(bookName, bookGenre, pageRequest);
        } else {
            bookPage = bookRepository.findAll(pageRequest);
        }

        return bookPage.map(bookMapper::bookToBookDto);
    }

    public Page<Book> listAllBooksByName(String bookName, Pageable pageable) {
        return bookRepository.findAllByBookNameIsLikeIgnoreCase("%" + bookName + "%", pageable);
    }

    public Page<Book> listAllBooksByGenre(BookGenre bookGenre, Pageable pageable) {
        return bookRepository.findAllByBookGenre(bookGenre, pageable);
    }

    public Page<Book> listAllBooksByNameAndGenre(String bookName, BookGenre bookGenre, Pageable pageable) {
        return bookRepository.findAllByBookNameIsLikeIgnoreCaseAndBookGenre("%" + bookName + "%", bookGenre,
                pageable);
    }

    @Override
    public Optional<BookDTO> getBookById(UUID bookId) {
        return Optional.ofNullable(bookMapper.bookToBookDto(bookRepository.findById(bookId).orElse(null)));
    }

    @Override
    public Optional<BookDTO> updateBookById(UUID bookId, BookDTO book) {
        AtomicReference<Optional<BookDTO>> atomicReference = new AtomicReference<>();

        bookRepository.findById(bookId).ifPresentOrElse(foundBook -> {
            foundBook.setBookIsbn(book.getBookIsbn());
            foundBook.setBookName(book.getBookName());
            foundBook.setBookGenre(book.getBookGenre());
            foundBook.setBookVersion(book.getBookVersion());
            foundBook.setBookUpdateDate(LocalDateTime.now());

            atomicReference.set(Optional.of(bookMapper.bookToBookDto(bookRepository.save(foundBook))));
        }, () -> {
            atomicReference.set(Optional.empty());
        });

        return atomicReference.get();
    }

    @Override
    public Optional<BookDTO> patchBookById(UUID bookId, BookDTO book) {
        AtomicReference<Optional<BookDTO>> atomicReference = new AtomicReference<>();

        bookRepository.findById(bookId).ifPresentOrElse(foundBook -> {
            if (book.getBookIsbn() != null)
                foundBook.setBookIsbn(book.getBookIsbn());
            if (StringUtils.hasText(book.getBookName()))
                foundBook.setBookName(book.getBookName());
            if (book.getBookGenre() != null)
                foundBook.setBookGenre(book.getBookGenre());
            if (book.getBookVersion() != null)
                foundBook.setBookVersion(book.getBookVersion());
            foundBook.setBookUpdateDate(LocalDateTime.now());

            atomicReference.set(Optional.of(bookMapper.bookToBookDto(bookRepository.save(foundBook))));
        }, () -> {
            atomicReference.set(Optional.empty());
        });

        return atomicReference.get();
    }

    @Override
    public Boolean deleteBookById(UUID bookId) {
        if (bookRepository.existsById(bookId)) {
            bookRepository.deleteById(bookId);
            return true;
        }

        return false;
    }

    public PageRequest buildPageRequest(Integer pageNumber, Integer pageSize) {
        int queryPageNumber;
        int queryPageSize;

        if (pageNumber != null && pageNumber > 0) {
            queryPageNumber = pageNumber - 1;
        } else {
            queryPageNumber = DEFAULT_PAGE;
        }

        if (pageSize != null) {
            if (pageSize > 1000) {
                queryPageSize = 1000;
            } else {
                queryPageSize = pageSize;
            }
        } else {
            queryPageSize = DEFAULT_PAGE_SIZE;
        }

        Sort sort = Sort.by(Sort.Order.asc("bookName"));

        return PageRequest.of(queryPageNumber, queryPageSize, sort);
    }
}
