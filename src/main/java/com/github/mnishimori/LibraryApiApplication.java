package com.github.mnishimori;

import com.github.mnishimori.domain.mail.EmailService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Arrays;
import java.util.List;

@SpringBootApplication
@EnableScheduling
public class LibraryApiApplication extends SpringBootServletInitializer {
    /*
    @Autowired
    private EmailService emailService;
    */

    public static void main(String[] args) {
        SpringApplication.run(LibraryApiApplication.class, args);
    }

    @Bean
    public ModelMapper modelMapper(){
        return new ModelMapper();
    }

    /*
    @Bean
    public CommandLineRunner runner(){
        return args -> {
            List<String> emails = Arrays.asList("cef04bf455-7a9c2f@inbox.mailtrap.io");
            emailService.sendEmails(emails, "Teste");
            System.out.println("EMAIL ENVIADO");
        };
    }
    */
}
