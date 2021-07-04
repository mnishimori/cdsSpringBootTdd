package com.github.mnishimori.api.controller;

import com.github.mnishimori.api.dto.LoanDto;
import com.github.mnishimori.api.dto.ReturnedLoanDto;
import com.github.mnishimori.domain.book.Book;
import com.github.mnishimori.domain.book.BookServiceImpl;
import com.github.mnishimori.domain.loan.Loan;
import com.github.mnishimori.domain.loan.LoanService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/loans")
public class LoanController {

    @Autowired
    private LoanService loanService;

    @Autowired
    private BookServiceImpl bookService;

    @Autowired
    private ModelMapper modelMapper;


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LoanDto create(@RequestBody LoanDto loanDto){

        Book book = bookService.getBookByIsbn(loanDto.getIsbn())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Book not found for passed isbn"));

        Loan loan = Loan
                .builder()
                .book(book)
                .customer(loanDto.getCustomer())
                .loanDate(LocalDate.now())
                .build();

        loan = loanService.save(loan);

        return this.modelMapper.map(loan, LoanDto.class);
    }


    @PatchMapping("/{id}")
    public void returnedBook(@PathVariable Long id, @RequestBody ReturnedLoanDto returnedLoanDto){

        Loan loan = loanService.getById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Book not found for passed isbn"));

        loan.setReturned(returnedLoanDto.getReturned());

        loanService.update(loan);
    }
}
