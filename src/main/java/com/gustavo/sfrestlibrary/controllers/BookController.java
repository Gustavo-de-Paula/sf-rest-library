package com.gustavo.sfrestlibrary.controllers;

import com.gustavo.sfrestlibrary.exceptions.NotFoundException;
import com.gustavo.sfrestlibrary.model.BookDTO;
import com.gustavo.sfrestlibrary.model.BookGenre;
import com.gustavo.sfrestlibrary.services.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class BookController {
    public static final String BOOK_PATH = "/api/v1/book";
    public static final String BOOK_PATH_ID = BOOK_PATH + "/{bookId}";

    private final BookService bookService;

    @PostMapping(value = BOOK_PATH)
    public ResponseEntity createBook(@Validated @RequestBody BookDTO bookDTO) {
        BookDTO savedBook = bookService.createBook(bookDTO);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Location", BOOK_PATH + "/" + savedBook.getBookId().toString());

        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }

    @GetMapping(value = BOOK_PATH)
    public Page<BookDTO> listBooks(@RequestParam(required = false) Long bookIsbn,
                                   @RequestParam(required = false) String bookName,
                                   @RequestParam(required = false) BookGenre bookGenre,
                                   @RequestParam(required = false) Integer pageNumber,
                                   @RequestParam(required = false) Integer pageSize) {
        return bookService.listAllBooks(bookIsbn, bookName, bookGenre, pageNumber, pageSize);
    }

    @GetMapping(value = BOOK_PATH_ID)
    public BookDTO getBookById(@PathVariable("bookId") UUID bookID) {
        return bookService.getBookById(bookID).orElseThrow(NotFoundException::new);
    }

    @PutMapping(value = BOOK_PATH_ID)
    public ResponseEntity updateBookById(@PathVariable("bookId") UUID bookId, @Validated @RequestBody BookDTO bookDTO) {
        if (bookService.updateBookById(bookId, bookDTO).isEmpty())
            throw new NotFoundException();

        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @PatchMapping(value = BOOK_PATH_ID)
    public ResponseEntity patchBookById(@PathVariable("bookId") UUID bookId, @RequestBody BookDTO bookDTO) {
        if (bookService.updateBookById(bookId, bookDTO).isEmpty())
            throw new NotFoundException();

        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping(value = BOOK_PATH_ID)
    public ResponseEntity deleteBookById(@PathVariable("bookId") UUID bookId) {
        if (!bookService.deleteBookById(bookId))
            throw new NotFoundException();

        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }
}
