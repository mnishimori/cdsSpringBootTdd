package com.github.mnishimori.domain.loan;

import com.github.mnishimori.domain.book.Book;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
public class LoanRepositoryTest {

    @Autowired
    private LoanRepository repository;

    @Autowired
    private TestEntityManager entityManager;

    private Book book;

    private Loan loan;


    @BeforeEach
    public void setUp(){
        book = this.createBook();
        entityManager.persist(book);

        loan = this.createLoan();
        loan.setBook(book);
        entityManager.persist(loan);
    }

    @Test
    @DisplayName("Deve verificar se existe empréstimo não devolvido para o livro")
    public void existsByBookAndNotReturned() {
        // cenário
        /*Book book = this.createBook();
        entityManager.persist(book);

        Loan loan = this.createLoan();
        entityManager.persist(loan);*/

        // execução
        Boolean exists = repository.existsByBookAndNotReturned(book);

        // verificação
        Assertions.assertThat(exists).isTrue();
    }


    @Test
    @DisplayName("Deve buscar empréstimo pelo isbn do livro ou customer")
    public void findByBookIsbnCustomerTest(){
        // cenário
        /*Book book = this.createBook();
        entityManager.persist(book);

        Loan loan = this.createLoan();
        entityManager.persist(loan);*/

        // execução
        Page<Loan> result = repository.findAll(Example.of(loan), PageRequest.of(0, 10));

        // verificação
        Assertions.assertThat(result.getContent()).hasSize(1);
        Assertions.assertThat(result.getContent()).contains(loan);
        Assertions.assertThat(result.getPageable().getPageSize()).isEqualTo(10);
        Assertions.assertThat(result.getPageable().getPageNumber()).isEqualTo(0);
        Assertions.assertThat(result.getTotalElements()).isEqualTo(1);
    }

    private Book createBook() {
        return Book.builder()
                .isbn("123")
                .author("Fulano")
                .title("As aventuras")
                .build();
    }

    private Loan createLoan(){
        Book book = this.createBook();
        book.setId(1L);

        return Loan
                .builder()
                .book(book)
                .customer("Fulano")
                .loanDate(LocalDate.now())
                .build();
    }
}
