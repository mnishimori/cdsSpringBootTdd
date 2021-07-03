package com.github.mnishimori.api.controller;

import com.github.mnishimori.api.dto.BookDto;
import com.github.mnishimori.domain.book.Book;
import com.github.mnishimori.domain.book.BookService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
@Slf4j
public class BookController {

    @Autowired
    private BookService service;

    @Autowired
    private ModelMapper modelMapper;


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookDto salvar(@RequestBody @Valid BookDto bookDto) {

        Book book = modelMapper.map(bookDto, Book.class);

        book = service.save(book);

        BookDto savedBookDto = modelMapper.map(book, BookDto.class);

        return savedBookDto;
    }


    @GetMapping
    public List<BookDto> listAll(){

        List<Book> books = service.listAll();

        List<BookDto> booksDto = books.stream().map(b -> {
            return modelMapper.map(b, BookDto.class);
        }).collect(Collectors.toList());

        return booksDto;
    }


    @GetMapping("{id}")
    public BookDto getById(@PathVariable Long id) {
        Book book = service.getById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        return modelMapper.map(book, BookDto.class);
    }


    @GetMapping("/lista-paginada")
    public Page<BookDto> search(BookDto bookDto, Pageable pageable){

        Book filter = this.modelMapper.map(bookDto, Book.class);

        Page<Book> result = this.service.find(filter, pageable);

        List<BookDto> returnList = result.getContent()
                .stream()
                .map( b -> this.modelMapper.map( b, BookDto.class))
                .collect(Collectors.toList());

        return new PageImpl<BookDto>(returnList, pageable, result.getTotalElements());
    }


    @PutMapping("{id}")
    public BookDto update(@PathVariable Long id, @RequestBody BookDto bookDto) {
        return service.getById(id).map(book -> {

            book.setAuthor(bookDto.getAuthor());
            book.setTitle(bookDto.getTitle());
            book = service.update(book);

            return modelMapper.map(book, BookDto.class);

        }).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }


    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id){
        Book book = service.getById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        service.delete(book);
    }
}
