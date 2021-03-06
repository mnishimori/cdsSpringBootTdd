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
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDate;
import java.util.Arrays;
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
    @DisplayName("Deve realizar um empr??stimo")
    public void createLoanTest() throws Exception {
        // cen??rio
        LoanDto loanDto = LoanDto
                .builder()
                .isbn("123")
                .customer("Fulano")
                .customerEmail("fulano@email.com")
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

        // execu????o
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(LOAN_API)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        // verifica????o
        mvc
                .perform(request)
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("id").isNotEmpty());
    }


    @Test
    @DisplayName("Deve retornar erro ao tentar fazer empr??stimo de um livro inexistente")
    public void invalidIsbnLoanTest() throws Exception {
        // cen??rio
        LoanDto loanDto = LoanDto
                .builder()
                .isbn("123")
                .customer("Fulano")
                .build();

        String json = new ObjectMapper().writeValueAsString(loanDto);

        BDDMockito
                .given(bookService.getBookByIsbn("123"))
                .willReturn(Optional.empty());

        // execu????o
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(LOAN_API)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        // verifica????o
        mvc
                .perform(request)
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("errors", Matchers.hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("errors[0]").value("Book not found for passed isbn"));
    }


    @Test
    @DisplayName("Deve retornar erro ao tentar fazer o empr??stimo de um livro j?? emprestado")
    public void loanedBookErrorOnCreateLoanTest() throws Exception {
        // cen??rio
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

        // execu????o
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(LOAN_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        // verifica????o
        mvc
                .perform(request)
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("errors", Matchers.hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("errors[0]").value("Book already loaned"));
    }


    @Test
    @DisplayName("Deve retornar um livro")
    public void returnBookTest() throws Exception {
        // cen??rio
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

        Mockito.verify(loanService, Mockito.times(1)).update(loan);
    }

    @Test
    @DisplayName("Deve retornar not found ao tentar retornar um livro inexistente")
    public void returnInexistentBookTest() throws Exception {
        // cen??rio
        ReturnedLoanDto returnedLoanDto = ReturnedLoanDto.builder().returned(true).build();

        BDDMockito
                .given(loanService.getById(Mockito.anyLong()))
                .willReturn(Optional.empty());

        String json = new ObjectMapper().writeValueAsString(returnedLoanDto);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .patch(LOAN_API.concat("/1"))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        mvc
                .perform(request)
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }


    @Test
    @DisplayName("Deve filtrar empr??stimos")
    public void findLoanTest() throws Exception {
        // cen??rio
        Long id = 1L;
        Loan loan = this.createLoan();
        loan.setId(id);

        BDDMockito
                .given(loanService.find(Mockito.any(Loan.class), Mockito.any(Pageable.class)))
                .willReturn(new PageImpl<Loan>(Arrays.asList(loan), PageRequest.of(0,10), 1));

        String queryString = String.format("?isbn=%s&customer=%s&page=0&size=10",
                loan.getBook().getIsbn(), loan.getCustomer());

        // execu????o
        MockHttpServletRequestBuilder request  = MockMvcRequestBuilders
                .get(LOAN_API.concat(queryString))
                .accept(MediaType.APPLICATION_JSON);

        // verifica????o
        mvc
                .perform(request)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("content", Matchers.hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("totalElements").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("pageable.pageSize").value(10))
                .andExpect(MockMvcResultMatchers.jsonPath("pageable.pageNumber").value(0));
    }


    @Test
    @DisplayName("Deve pesquisar livros emprestados por par??metros com pagina????o")
    public void searchBookTest() throws Exception {
        // cen??rio
        Book book = this.getBook();

        Loan loan = this.createLoan();

        BDDMockito.given(loanService.getLoansByBook(Mockito.any(Book.class), Mockito.any(Pageable.class)))
                .willReturn(new PageImpl<Loan>(Arrays.asList(loan), PageRequest.of(0, 100), 1));

        // execu????o
        String queryString = String.format("?title=%s&author=%s&page=0&size=100",
                book.getTitle(), book.getAuthor());

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(LOAN_API.concat("/" + book.getId() + "/loans"))
                .accept(MediaType.APPLICATION_JSON);

        // verifica????o
        mvc
                .perform(request)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("content", Matchers.hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("totalElements").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("pageable.pageSize").value(20))
                .andExpect(MockMvcResultMatchers.jsonPath("pageable.pageNumber").value(0));
    }



    private Loan createLoan(){
        return Loan
                .builder()
                .book(this.getBook())
                .customer("Fulano")
                .loanDate(LocalDate.now())
                .build();
    }

    private Book getBook() {
        return Book
                .builder()
                .id(1L)
                .isbn("123")
                .build();
    }

}
