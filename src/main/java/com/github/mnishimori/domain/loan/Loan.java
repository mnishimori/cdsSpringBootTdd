package com.github.mnishimori.domain.loan;

import com.github.mnishimori.domain.book.Book;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table
public class Loan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String customer;

    @ManyToOne
    @JoinColumn(name = "id_book")
    private Book book;

    private LocalDate loanDate;

    private Boolean returned;
}
