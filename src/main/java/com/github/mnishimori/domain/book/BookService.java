package com.github.mnishimori.domain.book;

import com.github.mnishimori.api.dto.BookDto;
import com.github.mnishimori.domain.loan.Loan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface BookService {

    Book save(Book book);

    List<Book> listAll();

    Optional<Book> getById(Long id);

    void delete(Book book);

    Book update(Book book);

    Page<Book> find(Book any, Pageable pageRequest);

    Optional<Book> getBookByIsbn(String isbn);

    Page<Loan> getLoansByBook(Book book, Pageable pageRequest);
}
