package com.github.mnishimori.domain.loan;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface LoanService {
    Loan save(Loan loan);

    Optional<Loan> getById(long id);

    Loan update(Loan loan);

    Page<Loan> find(Loan loan, Pageable pageable);
}
