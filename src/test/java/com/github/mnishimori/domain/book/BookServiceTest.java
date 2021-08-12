package com.github.mnishimori.domain.book;

import com.github.mnishimori.domain.exception.BusinessException;
import com.github.mnishimori.domain.loan.Loan;
import com.github.mnishimori.domain.loan.LoanRepository;
import com.github.mnishimori.domain.loan.LoanService;
import com.github.mnishimori.domain.loan.LoanServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class BookServiceTest {

    BookService service;

    @MockBean
    BookRepository repository;

    @BeforeEach
    public void setUp(){
        this.service = new BookServiceImpl(repository);
    }

    @Test
    @DisplayName("Deve salvar um livro")
    public void saveBookTest(){
        // cenário
        Book book = this.createBook();

        Mockito.when(repository.existsByIsbn(Mockito.anyString())).thenReturn(false);

        Mockito.when(repository.save(book))
                .thenReturn(
                        Book.builder()
                                .id(1L)
                                .isbn("123")
                                .title("As aventuras")
                                .author("Fulano")
                                .build());

        // execução
        Book savedBook = service.save(book);

        // verificação
        Assertions.assertThat(savedBook.getId()).isNotNull();
        Assertions.assertThat(savedBook.getIsbn()).isEqualTo("123");
        Assertions.assertThat(savedBook.getTitle()).isEqualTo("As aventuras");
        Assertions.assertThat(savedBook.getAuthor()).isEqualTo("Fulano");
    }


    @Test
    @DisplayName("Deve lançar erro ao tentar cadastrar um livro com isbn já utilizado")
    public void shouldNotSaveABookWithDuplicateIsbn(){

        // cenário
        Book book = this.createBook();
        Mockito.when(repository.existsByIsbn(Mockito.anyString())).thenReturn(true);

        // execução
        Throwable exception = Assertions.catchThrowable(() -> service.save(book));

        // verificação
        Assertions.assertThat(exception).isInstanceOf(BusinessException.class).hasMessage("Isbn já cadastrado");
        Mockito.verify(repository, Mockito.never()).save(book);
    }


    @Test
    @DisplayName("Deve retornar todos os livros")
    public void listAllTest() {
        // cenário
        Book book = this.createBook();
        book.setId(1L);
        List<Book> books = new ArrayList<>();
        books.add(book);
        Mockito.when(repository.findAll()).thenReturn(books);

        // execução
        List<Book> booksTest = service.listAll();;

        // verificação
        Assertions.assertThat(booksTest.size()).isGreaterThan(0);
        Assertions.assertThat(booksTest.size()).isEqualTo(books.size());
    }


    @Test
    @DisplayName("Deve obter um livro por id")
    public void getBookByIdTest() {
        // cenário
        Long id = 1l;

        Book book = this.createBook();
        book.setId(id);

        Mockito.when(repository.findById(id)).thenReturn(Optional.of(book));

        // execução
        Optional<Book> foundBook = service.getById(id);

        // verificação
        Assertions.assertThat(foundBook.isPresent()).isTrue();
        Assertions.assertThat(foundBook.get().getId()).isEqualTo(id);
        Assertions.assertThat(foundBook.get().getAuthor()).isEqualTo(book.getAuthor());
        Assertions.assertThat(foundBook.get().getTitle()).isEqualTo(book.getTitle());
        Assertions.assertThat(foundBook.get().getIsbn()).isEqualTo(book.getIsbn());
    }


    @Test
    @DisplayName("Deve retornar vazio ao obter um livro por id quando não existe na base")
    public void shouldReturnBookNotFound(){
        // cenário
        Long id = 1l;

        Mockito.when(repository.findById(id)).thenReturn(Optional.empty());

        // execução
        Optional<Book> book = service.getById(id);

        // verificação
        Assertions.assertThat(book.isPresent()).isFalse();
    }


    @Test
    @DisplayName("Deve filtrar livros pelos atributos do objeto")
    public void findBookTest(){
        // cenário
        Book book = this.createBook();

        PageRequest pageRequest = PageRequest.of(0, 10);

        List<Book> bookList = Arrays.asList(book);

        Page<Book> page = new PageImpl<Book>(bookList, pageRequest, 1);

        Mockito.when(repository.findAll(Mockito.any(Example.class), Mockito.any(PageRequest.class)))
                .thenReturn(page);

        // execução
        Page<Book> result = service.find(book, pageRequest);

        // verificação
        Assertions.assertThat(result.getTotalElements()).isEqualTo(1);
        Assertions.assertThat(result.getContent()).isEqualTo(bookList);
        Assertions.assertThat(result.getPageable().getPageNumber()).isEqualTo(0);
        Assertions.assertThat(result.getPageable().getPageSize()).isEqualTo(10);
    }




    @Test
    @DisplayName("Deve obter um livro pelo isbn")
    public void getBookByIsbnTest() {
        // cenário
        String isbn = "123";

        Mockito
                .when(repository.findByIsbn(isbn))
                .thenReturn(Optional.of(this.createBookWithId()));

        Optional<Book> book = service.getBookByIsbn(isbn);

        Assertions.assertThat(book.isPresent()).isTrue();
        Assertions.assertThat(book.get().getId().equals(1L));
        Assertions.assertThat(book.get().getIsbn()).isEqualTo(isbn);
    }


    @Test
    @DisplayName("Deve atualizar um livro")
    public void updateBookTest() {
        // cenário
        long id = 1l;
        Book updatingBook = Book.builder().id(id).build();

        Book book = this.createBook();
        book.setId(id);
        Mockito.when(repository.save(updatingBook)).thenReturn(book);

        // execução
        Book updatedBook = service.update(updatingBook);

        // verificação
        Assertions.assertThat(updatedBook.getId()).isEqualTo(book.getId());
        Assertions.assertThat(updatedBook.getTitle()).isEqualTo(book.getTitle());
        Assertions.assertThat(updatedBook.getAuthor()).isEqualTo(book.getAuthor());
        Assertions.assertThat(updatedBook.getIsbn()).isEqualTo(book.getIsbn());
    }


    @Test
    @DisplayName("Deve lançar execeção ao tentar atualizar um livro inexistente")
    public void updateInvalidBookTest() {
        // cenário
        Book book = new Book();

        // execução
        org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class, () -> service.update(book));

        // verificação
        Mockito.verify(repository, Mockito.never()).save(book);
    }


    @Test
    @DisplayName("Deve deletar um livro")
    public void deleteBookTest(){
        // cenário
        Book book = this.createBook();
        book.setId(1l);

        // execução
        org.junit.jupiter.api.Assertions.assertDoesNotThrow(() -> service.delete(book));

        // verificação
        Mockito.verify(repository, Mockito.times(1)).delete(book);
    }


    @Test
    @DisplayName("Deve lançar execeção ao tentar deletar um livro inexistente")
    public void deleteInvalidBookTest() {
        // cenário
        Book book = new Book();

        // execução
        org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class, () -> service.delete(book));

        // verificação
        Mockito.verify(repository, Mockito.never()).delete(book);
    }

    private Loan createNewLoan(){
        return Loan
                .builder()
                .book(this.createBook())
                .customer("Fulano")
                .loanDate(LocalDate.now())
                .build();
    }

    private Book createBook() {
        return Book.builder()
                .isbn("123")
                .author("Fulano")
                .title("As aventuras")
                .build();
    }

    private Book createBookWithId() {
        return Book.builder()
                .id(1L)
                .isbn("123")
                .author("Fulano")
                .title("As aventuras")
                .build();
    }
}
