package com.github.mnishimori.domain.book;

import java.util.List;
import java.util.Optional;

public interface IBookService {

    Book save(Book book);

    List<Book> listAll();

    Optional<Book> getById(Long id);

    void delete(Book book);
}
