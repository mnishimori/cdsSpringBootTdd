package com.github.mnishimori.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.mnishimori.api.dto.BookDto;
import com.github.mnishimori.domain.book.Book;
import com.github.mnishimori.domain.book.BookService;
import com.github.mnishimori.domain.exception.BusinessException;
import com.github.mnishimori.domain.loan.Loan;
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

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@WebMvcTest(controllers = BookController.class)
@AutoConfigureMockMvc
public class BookControllerTest {

    static String BOOK_API = "/api/books";

    @Autowired
    MockMvc mvc;

    @MockBean
    BookService service;


    @Test
    @DisplayName("Deve criar um livro com sucesso.")
    public void createBookTest() throws Exception {
        // cen??rio
        BookDto bookDto = this.createNewBookDto();

        Book savedBook = this.createNewBook();

        // execu????o
        BDDMockito.given(
                    service.save(Mockito.any(Book.class)))
                .willReturn(savedBook);

        String json = new ObjectMapper().writeValueAsString(bookDto);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(BOOK_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        // verifica????o
        mvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("id").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("title").value(bookDto.getTitle()))
                .andExpect(MockMvcResultMatchers.jsonPath("author").value(bookDto.getAuthor()))
                .andExpect(MockMvcResultMatchers.jsonPath("isbn").value(bookDto.getIsbn()));
    }

    @Test
    @DisplayName("Deve lan??ar erro de valida????o quando n??o houver dados suficientes para a cria????o do livro.")
    public void createInvalidBookTest() throws Exception {
        // cen??rio
        String json = new ObjectMapper().writeValueAsString(new BookDto());

        // execu????o
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(BOOK_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        // verifica????o
        mvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("errors", Matchers.hasSize(3)));
    }


    @Test
    @DisplayName("Deve lan??ar erro ao tentar cadastrar um livro com isbn j?? utilizado")
    public void createBookWithDuplicateIsbn() throws Exception {
        // cen??rio
        BookDto bookDto = this.createNewBookDto();

        String json = new ObjectMapper().writeValueAsString(bookDto);

        String mensagemErro = "Isbn j?? cadastrado";

        // execu????o
        BDDMockito.given(service.save(Mockito.any(Book.class))).willThrow(new BusinessException(mensagemErro));

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(BOOK_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        // verifica????o
        mvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("errors", Matchers.hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("errors[0]").value(mensagemErro));
    }


    @Test
    @DisplayName("Deve listar todos os livros")
    public void listAllTest() throws Exception {
        // cen??rio
        Book book = this.createNewBook();

        BDDMockito
                .given(service.listAll())
                .willReturn(Arrays.asList(book));

        // execu????o
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(BOOK_API)
                .accept(MediaType.APPLICATION_JSON);

        // verifica????o
        mvc
                .perform(request)
                .andExpect(MockMvcResultMatchers.status().isOk());
    }


    @Test
    @DisplayName("Deve obter informa????es de um livro")
    public void getBookDetailsTest() throws Exception {
        // cen??rio
        Book book = this.createNewBook();

        BDDMockito.given(service.getById(book.getId())).willReturn(Optional.of(book));

        // execu????o
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(BOOK_API.concat("/" + book.getId()))
                .accept(MediaType.APPLICATION_JSON);

        // verifica????o
        mvc
            .perform(request)
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("id").value(book.getId()))
            .andExpect(MockMvcResultMatchers.jsonPath("title").value(book.getTitle()))
            .andExpect(MockMvcResultMatchers.jsonPath("author").value(book.getAuthor()))
            .andExpect(MockMvcResultMatchers.jsonPath("isbn").value(book.getIsbn()));
    }


    @Test
    @DisplayName("Deve retornar resource not found quando o livro procurado n??o existir")
    public void bookNotFoundTest() throws Exception {
        // cen??rio
        BDDMockito.given(service.getById(Mockito.anyLong())).willReturn(Optional.empty());

        // execu????o
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(BOOK_API.concat("/" + 1))
                .accept(MediaType.APPLICATION_JSON);

        // verifica????o
        mvc
                .perform(request)
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }


    @Test
    @DisplayName("Deve filtrar livros")
    public void findBookTest() throws Exception {
        // cen??rio
        Long id = 1l;

        Book book = this.createNewBook();

        BDDMockito.given(service.find(Mockito.any(Book.class), Mockito.any(Pageable.class)))
                .willReturn(new PageImpl<Book>(Arrays.asList(book), PageRequest.of(0, 100), 1));

        // execu????o
        String queryString = String.format("?title=%s&author=%s&page=0&size=100",
                book.getTitle(), book.getAuthor());

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(BOOK_API.concat("/lista-paginada/").concat(queryString))
                .accept(MediaType.APPLICATION_JSON);

        // verifica????o
        mvc
                .perform(request)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("content", Matchers.hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("totalElements").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("pageable.pageSize").value(100))
                .andExpect(MockMvcResultMatchers.jsonPath("pageable.pageNumber").value(0));
    }


    @Test
    @DisplayName("Deve atualizar um livro")
    public void updateBookTest() throws Exception {
        // cen??rio
        Long id = 1L;

        String json = new ObjectMapper().writeValueAsString(this.createNewBookDto());

        Book updatingBook = Book.builder().id(1l).title("some title").author("some author").isbn("321").build();

        BDDMockito.given(service.getById(id)).willReturn(Optional.of(updatingBook));

        Book updatedBook = Book.builder().id(id).author("Arthur").title("As aventuras").isbn("321").build();

        BDDMockito.given(service.update(updatingBook)).willReturn(updatedBook);

        // execu????o
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .put(BOOK_API.concat("/" + 1))
                .content(json)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        // verifica????o
        mvc
                .perform(request)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("id").value(updatedBook.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("title").value(updatedBook.getTitle()))
                .andExpect(MockMvcResultMatchers.jsonPath("author").value(updatedBook.getAuthor()))
                .andExpect(MockMvcResultMatchers.jsonPath("isbn").value("321"));
    }


    @Test
    @DisplayName("Deve lan??ar exce????o e retornar status 404 ao tentar atualizar um livro inexistente")
    public void updateNotFoundBookTest() throws Exception {
        // cen??rio
        String json = new ObjectMapper().writeValueAsString(this.createNewBookDto());

        Book updatingBook = Book.builder().id(1l).title("some title").author("some author").isbn("321").build();

        BDDMockito.given(service.getById(Mockito.anyLong())).willReturn(Optional.empty());

        // execu????o
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .put(BOOK_API.concat("/" + 1))
                .content(json)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        // verifica????o
        mvc
                .perform(request)
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }


    @Test
    @DisplayName("Deve deletar um livro")
    public void deleteBookTest() throws Exception {
        // cen??rio
        Book book = this.createNewBook();

        BDDMockito.given(service.getById(Mockito.anyLong())).willReturn(Optional.of(book));

        // execu????o
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .delete(BOOK_API.concat("/" + book.getId()));

        // verifica????o
        mvc
                .perform(request)
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }


    @Test
    @DisplayName("Deve retornar resource not found quando o livro procurado n??o existir para deletar")
    public void deleteNotFoundBookTest() throws Exception {
        // cen??rio
        BDDMockito.given(service.getById(Mockito.anyLong())).willReturn(Optional.empty());

        // execu????o
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .delete(BOOK_API.concat("/" + 1));

        // verifica????o
        mvc
                .perform(request)
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }


    private BookDto createNewBookDto() {
        BookDto bookDto = BookDto.builder()
                .author("Artur")
                .title("As aventuras")
                .isbn("001")
                .build();
        return bookDto;
    }

    private Book createNewBook() {
        Book savedBook = Book.builder()
                .id(1L)
                .author("Artur")
                .title("As aventuras")
                .isbn("001")
                .build();
        return savedBook;
    }

    private Loan createNewLoan(){
        return Loan
                .builder()
                .book(this.createNewBook())
                .customer("Fulano")
                .loanDate(LocalDate.now())
                .build();
    }
}
