package com.github.mnishimori.domain.loan;

import com.github.mnishimori.domain.exception.BusinessException;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class LoanServiceImpl implements LoanService {

    LoanRepository repository;

    public LoanServiceImpl(LoanRepository repository) {
        this.repository = repository;
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
}
