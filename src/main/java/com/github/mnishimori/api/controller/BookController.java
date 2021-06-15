package com.github.mnishimori.api.controller;

import com.github.mnishimori.api.dto.BookDto;
import com.github.mnishimori.domain.book.Book;
import com.github.mnishimori.domain.book.IBookService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {

    private final IBookService service;

    private final ModelMapper modelMapper;


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookDto salvar(@RequestBody @Valid BookDto bookDto) {

        Book book = modelMapper.map(bookDto, Book.class);

        book = service.save(book);

        BookDto savedBookDto = modelMapper.map(book, BookDto.class);

        return savedBookDto;
    }
}
