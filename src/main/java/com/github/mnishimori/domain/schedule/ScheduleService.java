package com.github.mnishimori.domain.schedule;

import com.github.mnishimori.domain.loan.Loan;
import com.github.mnishimori.domain.loan.LoanService;
import com.github.mnishimori.domain.mail.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ScheduleService {

    @Autowired
    private LoanService loanService;

    @Autowired
    private EmailService emailService;


    @Scheduled(cron = "0 0 0 1/1 * ?")
    public void sendMailToLateLoans(){
        List<Loan> allLateLoans = loanService.getAllLateLoans();

        List<String> emails = allLateLoans
                .stream()
                .map(loan -> loan.getCustomerEmail())
                .collect(Collectors.toList());

        String message = "Atenão! Você tem um empréstimo atrasado. Favor devolver o livro o mais rápido possível";

        emailService.sendEmails(emails, message);
    }

}
