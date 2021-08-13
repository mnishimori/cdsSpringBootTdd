package com.github.mnishimori.domain.loan;

import com.github.mnishimori.domain.book.Book;
import com.github.mnishimori.domain.book.BookRepository;
import com.github.mnishimori.domain.book.BookService;
import com.github.mnishimori.domain.exception.BusinessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class LoanServiceImpl implements LoanService {

    private LoanRepository repository;

    private BookRepository bookRepository;

    public LoanServiceImpl(LoanRepository repository, BookRepository bookRepository) {
        this.repository = repository;
        this.bookRepository = bookRepository;
    }

    @Override
    public Loan save(Loan loan) {
        if (repository.existsByBookAndNotReturned(loan.getBook())){
            throw new BusinessException("Book already loaned");
        }
        return repository.save(loan);
    }

    @Override
    public Optional<Loan> getById(long id) {
        return repository.findById(id);
    }

    @Override
    public Loan update(Loan loan) {

        return repository.save(loan);
    }

    @Override
    public Page<Loan> find(Loan loan, Pageable pageable) {
        Example<Loan> example = Example.of(loan, ExampleMatcher
                .matching()
                .withIgnoreCase()
                .withIgnoreNullValues()
                .withStringMatcher( ExampleMatcher.StringMatcher.CONTAINING ));

        return repository.findAll(example, pageable);
    }

    @Override
    public List<Loan> getAllLateLoans() {
        final Integer loanDays = 4;

        LocalDate threeDaysAgo = LocalDate.now().minusDays(loanDays);

        return repository.findByLoanDateLessThanAndNotReturned(threeDaysAgo);
    }

    @Override
    public Page<Loan> getLoansByBook(Book book, Pageable pageable) {
        Book bookFound = bookRepository
                .findById(book.getId()).
                orElseThrow(() -> new EntityNotFoundException("Livro não encontrado"));

        Loan loanFilter = Loan.builder()
                .book(bookFound)
                .build();

        return this.find(loanFilter, pageable);
    }
}
