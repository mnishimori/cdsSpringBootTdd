package com.github.mnishimori.domain.book;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
public class BookRepositoryTest {

    @Autowired
    TestEntityManager entityManager;

    @Autowired
    BookRepository repository;


    @Test
    @DisplayName("Deve retornar verdadeiro quando existir um livro na base com o isbn informado")
    public void returnTrueWhenIsbnExists(){
        // cenário
        String isbn = "123";
        entityManager.persist(Book.builder().title("Aventuras").author("Fulano").isbn(isbn).build());

        // execução
        boolean exists = repository.existsByIsbn(isbn);

        // verificação
        Assertions.assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Deve retornar falso quando não existir um livro na base com o isbn informado")
    public void returnFalseWhenIsbnNotExists(){
        // cenário
        String isbn = "123";

        // execução
        boolean exists = repository.existsByIsbn(isbn);

        // verificação
        Assertions.assertThat(exists).isFalse();
    }


    @Test
    @DisplayName("Deve retornar um livro por id")
    public void shouldReturnBookByIdTest() {
        // cenário
        Book book = this.createNewBook();
        entityManager.persist(book);

        // execução
        Optional<Book> foundBook = repository.findById(book.getId());

        // verificação
        Assertions.assertThat(foundBook.isPresent()).isTrue();
    }


    @Test
    @DisplayName("Deve salvar um livro")
    public void saveBookTest(){
        // cenário
        Book book = this.createNewBook();

        // execução
        Book savedBook = repository.save(book);

        // verificação
        Assertions.assertThat(savedBook.getId()).isNotNull();
    }


    @Test
    @DisplayName("Deve deletar um livro")
    public void deleteBookTest(){
        // cenário
        Book book = this.createNewBook();
        entityManager.persist(book);

        Book foundBook = entityManager.find(Book.class, book.getId());

        // execução
        repository.delete(foundBook);

        // verificação
        Book deletedBook = entityManager.find(Book.class, book.getId());
        Assertions.assertThat(deletedBook).isNull();
    }


    private Book createNewBook(){

        return Book.builder()
                .title("Aventuras")
                .author("Fulano")
                .isbn("123")
                .build();
    }
}
