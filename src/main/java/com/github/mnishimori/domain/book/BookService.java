package com.github.mnishimori.domain.book;

import com.github.mnishimori.domain.exception.BusinessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sun.net.www.MimeTable;

import java.util.List;

@Service
public class BookService implements IBookService {

    private BookRepository repository;

    public BookService(BookRepository repository) {
        this.repository = repository;
    }

    @Override
    public Book save(Book book) {

        if (repository.existsByIsbn(book.getIsbn())) {
            throw new BusinessException("Isbn já cadastrado");
        }

        return repository.save(book);
    }

    @Override
    public List<Book> listAll() {

        return repository.findAll();
    }
}
