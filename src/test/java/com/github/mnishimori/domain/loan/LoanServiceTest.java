package com.github.mnishimori.domain.loan;

import com.github.mnishimori.domain.book.Book;
import com.github.mnishimori.domain.exception.BusinessException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.Optional;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class LoanServiceTest {

    LoanService service;

    @MockBean
    private LoanRepository repository;

    @BeforeEach
    public void setUp(){
        this.service = new LoanServiceImpl(repository);
    }

    @Test
    @DisplayName("Deve salvar um empréstimo")
    public void saveLoanTest(){
        // cenário
        Loan savingLoan = this.createLoan();

        Loan savedLoan = this.createLoan();
        savedLoan.setId(1L);

        Mockito
                .when(repository.existsByBookAndNotReturned(savingLoan.getBook()))
                .thenReturn(false);

        Mockito
                .when(repository.save(savingLoan))
                .thenReturn(savedLoan);

        // execução
        Loan loan = service.save(savingLoan);

        // verificação
        Assertions.assertThat(loan.getId()).isEqualTo(savedLoan.getId());
        Assertions.assertThat(loan.getBook().getId()).isEqualTo(savedLoan.getBook().getId());
        Assertions.assertThat(loan.getCustomer()).isEqualTo(savedLoan.getCustomer());
        Assertions.assertThat(loan.getLoanDate()).isEqualTo(savedLoan.getLoanDate());
    }


    @Test
    @DisplayName("Deve lançar erro de negócio ao salvar um empréstimo com livro já emprestado")
    public void loanedBookSaveTest(){
        // cenário
        Loan savingLoan = this.createLoan();

        Mockito
                .when(repository.existsByBookAndNotReturned(savingLoan.getBook()))
                .thenReturn(true);

        // execução
        Throwable exception = Assertions.catchThrowable(() -> service.save(savingLoan));

        // verificação
        Assertions
                .assertThat(exception)
                .isInstanceOf(BusinessException.class)
                .hasMessage("Book already loaned");

        Mockito.verify(repository, Mockito.never()).save(savingLoan);
    }


    @Test
    @DisplayName("Deve obter um empréstimo por id")
    public void getLoanById(){
        // cenário
        Long id = 1L;
        Loan loan = this.createLoan();
        loan.setId(id);

        Mockito
                .when(repository.findById(id))
                .thenReturn(Optional.of(loan));

        // execução
        Optional<Loan> loanFound = service.getById(id);

        // verificação
        Assertions.assertThat(loanFound.isPresent()).isTrue();
        Assertions.assertThat(loanFound.get().getId()).isEqualTo(loan.getId());
        Assertions.assertThat(loanFound.get().getBook()).isEqualTo(loan.getBook());
        Assertions.assertThat(loanFound.get().getCustomer()).isEqualTo(loan.getCustomer());
        Assertions.assertThat(loanFound.get().getLoanDate()).isEqualTo(loan.getLoanDate());
    }


    @Test
    @DisplayName("Deve atualizar um empréstimo")
    public void updateLoanTest() {
        // cenário
        Long id = 1L;
        Loan loan = this.createLoan();
        loan.setId(id);

        Mockito
                .when(repository.save(loan))
                .thenReturn(loan);

        // execução
        Loan updatedLoan = service.update(loan);

        // verificação
        Assertions.assertThat(updatedLoan.getId()).isEqualTo(loan.getId());
        Assertions.assertThat(updatedLoan.getBook()).isEqualTo(loan.getBook());
        Assertions.assertThat(updatedLoan.getCustomer()).isEqualTo(loan.getCustomer());
        Assertions.assertThat(updatedLoan.getLoanDate()).isEqualTo(loan.getLoanDate());
        Assertions.assertThat(updatedLoan.getReturned()).isEqualTo(loan.getReturned());
    }


    private Loan createLoan(){
        return Loan
                .builder()
                .book(this.createBook())
                .customer("Fulano")
                .loanDate(LocalDate.now())
                .build();
    }

    private Book createBook() {
        return Book
                .builder()
                .id(1L)
                .build();
    }

}
