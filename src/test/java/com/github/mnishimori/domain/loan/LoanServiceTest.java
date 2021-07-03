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
