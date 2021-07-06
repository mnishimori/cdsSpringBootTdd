package com.github.mnishimori.api.controller;

import com.github.mnishimori.api.dto.BookDto;
import com.github.mnishimori.api.dto.LoanDto;
import com.github.mnishimori.api.dto.ReturnedLoanDto;
import com.github.mnishimori.domain.book.Book;
import com.github.mnishimori.domain.book.BookServiceImpl;
import com.github.mnishimori.domain.loan.Loan;
import com.github.mnishimori.domain.loan.LoanService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

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


    @GetMapping
    public Page<LoanDto> find(LoanDto loanDto, Pageable pageRequest) {
        Loan loan = this.modelMapper.map(loanDto, Loan.class);

        Page<Loan> result = loanService.find(loan, pageRequest);

        List<LoanDto> loans = result.getContent().stream().map(l -> {
            LoanDto lDto = modelMapper.map(l, LoanDto.class);
            lDto.setBookDto(modelMapper.map(l.getBook(), BookDto.class));
            return lDto;
        }).collect(Collectors.toList());

        return new PageImpl<LoanDto>(loans, pageRequest, result.getTotalElements());
    }


    @PatchMapping("/{id}")
    public void returnedBook(@PathVariable Long id, @RequestBody ReturnedLoanDto returnedLoanDto){

        Loan loan = loanService.getById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        loan.setReturned(returnedLoanDto.getReturned());

        loanService.update(loan);
    }
}
