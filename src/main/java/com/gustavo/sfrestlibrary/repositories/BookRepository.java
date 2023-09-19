package com.gustavo.sfrestlibrary.repositories;

import com.gustavo.sfrestlibrary.entities.Book;
import com.gustavo.sfrestlibrary.model.BookGenre;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface BookRepository extends JpaRepository<Book, UUID> {
    Page<Book> findAllByBookNameIsLikeIgnoreCase(String bookName, Pageable pageable);
    Page<Book> findAllByBookGenre(BookGenre bookGenre, Pageable pageable);
    Page<Book> findAllByBookNameIsLikeIgnoreCaseAndBookGenre(String bookName, BookGenre bookGenre, Pageable pageable);
}
