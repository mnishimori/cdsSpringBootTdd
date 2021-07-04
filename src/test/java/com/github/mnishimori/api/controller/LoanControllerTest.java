package com.github.mnishimori.api.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.mnishimori.api.dto.LoanDto;
import com.github.mnishimori.api.dto.ReturnedLoanDto;
import com.github.mnishimori.domain.book.Book;
import com.github.mnishimori.domain.book.BookServiceImpl;
import com.github.mnishimori.domain.exception.BusinessException;
import com.github.mnishimori.domain.loan.Loan;
import com.github.mnishimori.domain.loan.LoanService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDate;
import java.util.Optional;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@WebMvcTest(controllers = LoanController.class)
@AutoConfigureMockMvc
public class LoanControllerTest {

    private static final String LOAN_API = "/api/loans";

    @Autowired
    private MockMvc mvc;

    @MockBean
    private BookServiceImpl bookService;

    @MockBean
    private LoanService loanService;


    @Test
    @DisplayName("Deve realizar um empréstimo")
    public void createLoanTest() throws Exception {
        // cenário
        LoanDto loanDto = LoanDto
                .builder()
                .isbn("123")
                .customer("Fulano")
                .build();

        String json = new ObjectMapper().writeValueAsString(loanDto);

        BDDMockito
                .given(bookService.getBookByIsbn("123"))
                .willReturn(Optional.of(this.getBook()));

        Loan loan = Loan.builder()
                .id(1L)
                .customer("Fulano")
                .book(this.getBook())
                .loanDate(LocalDate.now())
                .build();

        BDDMockito
                .given(loanService.save(Mockito.any(Loan.class)))
                .willReturn(loan);

        // execução
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(LOAN_API)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        // verificação
        mvc
                .perform(request)
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("id").isNotEmpty());
    }


    @Test
    @DisplayName("Deve retornar erro ao tentar fazer empréstimo de um livro inexistente")
    public void invalidIsbnLoanTest() throws Exception {
        // cenário
        LoanDto loanDto = LoanDto
                .builder()
                .isbn("123")
                .customer("Fulano")
                .build();

        String json = new ObjectMapper().writeValueAsString(loanDto);

        BDDMockito
                .given(bookService.getBookByIsbn("123"))
                .willReturn(Optional.empty());

        // execução
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(LOAN_API)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        // verificação
        mvc
                .perform(request)
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("errors", Matchers.hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("errors[0]").value("Book not found for passed isbn"));
    }


    @Test
    @DisplayName("Deve retornar erro ao tentar fazer o empréstimo de um livro já emprestado")
    public void loanedBookErrorOnCreateLoanTest() throws Exception {
        // cenário
        LoanDto loanDto = LoanDto
                .builder()
                .isbn("123")
                .customer("Fulano")
                .build();

        String json = new ObjectMapper().writeValueAsString(loanDto);

        BDDMockito
                .given(bookService.getBookByIsbn("123"))
                .willReturn(Optional.of(getBook()));

        BDDMockito
                .given(loanService.save(Mockito.any(Loan.class)))
                .willThrow(new BusinessException("Book already loaned"));

        // execução
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(LOAN_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        // verificação
        mvc
                .perform(request)
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("errors", Matchers.hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("errors[0]").value("Book already loaned"));
    }


    @Test
    @DisplayName("Deve retornar um livro")
    public void returnBookTest() throws Exception {
        // cenário
        ReturnedLoanDto returnedLoanDto = ReturnedLoanDto.builder().returned(true).build();

        Loan loan = Loan.builder().id(1L).build();

        BDDMockito
                .given(loanService.getById(Mockito.anyLong()))
                .willReturn(Optional.of(loan));

        String json = new ObjectMapper().writeValueAsString(returnedLoanDto);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .patch(LOAN_API.concat("/1"))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        mvc
                .perform(request)
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    private Book getBook() {
        return Book.builder().id(1L).isbn("123").build();
    }

}
