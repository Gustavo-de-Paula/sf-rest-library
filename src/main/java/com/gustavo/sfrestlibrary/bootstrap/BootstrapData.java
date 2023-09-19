package com.gustavo.sfrestlibrary.bootstrap;

import com.gustavo.sfrestlibrary.entities.Book;
import com.gustavo.sfrestlibrary.model.BookGenre;
import com.gustavo.sfrestlibrary.repositories.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;

@Component
@RequiredArgsConstructor
public class BootstrapData implements CommandLineRunner {
    private final BookRepository bookRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        loadBookData();
    }

    private void loadBookData() {
        if (bookRepository.count() == 0) {
            Book book1 = Book.builder()
                    .bookIsbn(1111111111111L)
                    .bookName("Neuromancer")
                    .bookGenre(BookGenre.SCIFI)
                    .bookVersion(1)
                    .bookCreationDate(LocalDateTime.now())
                    .bookUpdateDate(LocalDateTime.now())
                    .build();

            Book book2 = Book.builder()
                    .bookIsbn(2222222222222L)
                    .bookName("The Color Out of Space")
                    .bookGenre(BookGenre.HORROR)
                    .bookVersion(2)
                    .bookCreationDate(LocalDateTime.now())
                    .bookUpdateDate(LocalDateTime.now())
                    .build();

            Book book3 = Book.builder()
                    .bookIsbn(3333333333333L)
                    .bookName("Lord of the Rings: The Two Towers")
                    .bookGenre(BookGenre.FANTASY)
                    .bookVersion(3)
                    .bookCreationDate(LocalDateTime.now())
                    .bookUpdateDate(LocalDateTime.now())
                    .build();

            bookRepository.saveAll(Arrays.asList(book1, book2, book3));
        }
    }
}
