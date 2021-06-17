package com.github.mnishimori.domain.book;

import java.util.List;

public interface IBookService {

    Book save(Book book);

    List<Book> listAll();

}
