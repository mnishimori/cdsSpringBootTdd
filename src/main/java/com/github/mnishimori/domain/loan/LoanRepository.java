package com.github.mnishimori.domain.loan;

import com.github.mnishimori.domain.book.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface LoanRepository extends JpaRepository<Loan, Long> {

    @Query(value = " select case when ( count(l.id) > 0 ) then true else false end " +
            " from Loan l where l.book = :book and (l.returned is null or l.returned is false)")
    Boolean existsByBookAndNotReturned( @Param("book") Book book );

}
