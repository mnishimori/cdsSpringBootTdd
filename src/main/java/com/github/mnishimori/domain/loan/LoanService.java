package com.github.mnishimori.domain.loan;

import java.util.Optional;

public interface LoanService {
    Loan save(Loan loan);

    Optional<Loan> getById(long id);
}
