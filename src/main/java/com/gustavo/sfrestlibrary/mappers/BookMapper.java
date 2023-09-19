package com.gustavo.sfrestlibrary.mappers;

import com.gustavo.sfrestlibrary.entities.Book;
import com.gustavo.sfrestlibrary.model.BookDTO;
import org.mapstruct.Mapper;

@Mapper
public interface BookMapper {
    Book bookDtoToBook(BookDTO bookDTO);
    BookDTO bookToBookDto(Book book);
}
