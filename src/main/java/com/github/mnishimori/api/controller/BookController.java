package com.github.mnishimori.api.controller;

import com.github.mnishimori.api.dto.BookDto;
import com.github.mnishimori.api.exception.ApiErrors;
import com.github.mnishimori.domain.book.Book;
import com.github.mnishimori.domain.book.IBookService;
import com.github.mnishimori.domain.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {

    @Autowired
    private IBookService service;

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


    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErrors handleValidationException(MethodArgumentNotValidException ex) {

        return new ApiErrors(ex.getBindingResult());
    }

    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErrors handleValidationException(BusinessException ex) {

        return new ApiErrors(ex);
    }

}
