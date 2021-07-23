package com.github.mnishimori.domain.loan;

import com.github.mnishimori.domain.book.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface LoanRepository extends JpaRepository<Loan, Long> {

    @Query(value = " select case when ( count(l.id) > 0 ) then true else false end as valor from Loan l where (l.returned is null or l.returned = false) and l.book = :book ")
    Boolean existsByBookAndNotReturned( @Param("book") Book book );

    @Query(value = "select l from Loan l where l.loanDate <= :threeDaysAgo and (l.returned is null or l.returned = false) ")
    List<Loan> findByLoanDateLessThanAndNotReturned(@Param("threeDaysAgo") LocalDate threeDaysAgo);
}
