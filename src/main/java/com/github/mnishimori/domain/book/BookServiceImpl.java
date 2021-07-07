package com.github.mnishimori.domain.book;

import com.github.mnishimori.domain.exception.BusinessException;
import com.github.mnishimori.domain.loan.Loan;
import com.github.mnishimori.domain.loan.LoanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;

@Service
public class BookServiceImpl implements BookService {

    private BookRepository repository;

    @Autowired
    private LoanService loanService;

    public BookServiceImpl(BookRepository repository) {
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

    @Override
    public Optional<Book> getById(Long id) {

        return repository.findById(id);
    }

    @Override
    public void delete(Book book) {
        if (book == null || book.getId() == null) {
            throw new IllegalArgumentException("Book id can't be null");
        }
        repository.delete(book);
    }

    @Override
    public Book update(Book book) {
        if (book == null || book.getId() == null) {
            throw new IllegalArgumentException("Book id can't be null");
        }
        return repository.save(book);
    }

    @Override
    public Page<Book> find(Book book, Pageable pageRequest) {

        Example<Book> example = Example.of(book, ExampleMatcher.matching()
                .withIgnoreCase()
                .withIgnoreNullValues()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING));

        return repository.findAll(example, pageRequest);
    }

    @Override
    public Optional<Book> getBookByIsbn(String isbn) {
        return repository.findByIsbn(isbn);
    }

    @Override
    public Page<Loan> getLoansByBook(Book book, Pageable pageRequest) {
        Book bookFound = this.getById(book.getId())
                .orElseThrow(() -> new EntityNotFoundException("Livro não encontrado"));

        Loan loanFilter = Loan.builder()
                .book(book)
                .build();

        return loanService.find(loanFilter, pageRequest);
    }
}
