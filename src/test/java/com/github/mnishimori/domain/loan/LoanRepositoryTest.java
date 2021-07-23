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
import java.util.List;

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
        // configurado no método setUp()

        // execução
        Boolean exists = repository.existsByBookAndNotReturned(book);

        // verificação
        Assertions.assertThat(exists).isTrue();
    }


    @Test
    @DisplayName("Deve buscar empréstimo pelo isbn do livro ou customer")
    public void findByBookIsbnCustomerTest(){
        // cenário
        // configurado no método setUp()

        // execução
        Page<Loan> result = repository.findAll(Example.of(loan), PageRequest.of(0, 10));

        // verificação
        Assertions.assertThat(result.getContent()).hasSize(1);
        Assertions.assertThat(result.getContent()).contains(loan);
        Assertions.assertThat(result.getPageable().getPageSize()).isEqualTo(10);
        Assertions.assertThat(result.getPageable().getPageNumber()).isEqualTo(0);
        Assertions.assertThat(result.getTotalElements()).isEqualTo(1);
    }


    @Test
    @DisplayName("Deve obter empréstimos não retornados em até três dias")
    public void findByLoanDateLessThanAndNotReturnedTest(){
        // cenário
        // configurado no método setUp()
        loan.setLoanDate(LocalDate.now().minusDays(5));
        loan.setReturned(false);
        entityManager.persist(loan);

        // execução
        List<Loan> loans = repository.findByLoanDateLessThanAndNotReturned(LocalDate.now().minusDays(4));

        // verificação
        Assertions.assertThat(loans).hasSize(1).contains(loan);
    }


    @Test
    @DisplayName("Não deve obter empréstimos não retornados em até três dias ")
    public void notFindByLoanDateLessThanAndNotReturned() {
        // cenário
        loan.setLoanDate(LocalDate.now().minusDays(3));
        loan.setReturned(false);
        entityManager.persist(loan);

        // execução
        List<Loan> loans = repository.findByLoanDateLessThanAndNotReturned(LocalDate.now().minusDays(4));

        // verificação
        Assertions.assertThat(loans).isEmpty();
    }

    public Loan createAndPersistLoan(LocalDate loanDate){
        Book book = createBook();
        entityManager.persist(book);

        Loan loan = Loan.builder().book(book).customer("Fulano").loanDate(loanDate).build();
        entityManager.persist(loan);

        return loan;
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
