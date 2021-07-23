package com.github.mnishimori.domain.mail;

import java.util.List;

public interface EmailService {

    void sendEmails(List<String> emails, String message);

}
