package com.github.mnishimori.domain.book;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sun.net.www.MimeTable;

@Service
public class BookService implements IBookService {

    private BookRepository repository;

    public BookService(BookRepository repository) {
        this.repository = repository;
    }

    @Override
    public Book save(Book book) {

        return repository.save(book);
    }
}
