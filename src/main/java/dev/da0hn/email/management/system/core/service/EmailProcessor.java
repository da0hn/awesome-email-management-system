package dev.da0hn.email.management.system.core.service;

import java.nio.charset.StandardCharsets;

import jakarta.mail.Flags;
import jakarta.mail.Folder;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.search.FlagTerm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EmailProcessor {

    private final JavaMailSenderImpl javaMailSender;

    public EmailProcessor(final JavaMailSender javaMailSender) {
        this.javaMailSender = (JavaMailSenderImpl) javaMailSender;
    }

    public void process() {
        final Session session = this.javaMailSender.getSession();
        try {
            final var store = session.getStore("imaps");
            store.connect(this.javaMailSender.getHost(), this.javaMailSender.getUsername(), this.javaMailSender.getPassword());
            final var inboxFolder = store.getFolder("INBOX");
            inboxFolder.open(Folder.READ_ONLY);
            final var messages = inboxFolder.search(new FlagTerm(new Flags(Flags.Flag.DELETED), false));

            for (int i = 0; i < 10; i++) {
                final var message = messages[i];
                log.info("From: {}, Subject: {}", message.getFrom(), message.getSubject().getBytes(StandardCharsets.UTF_8));
            }
        }
        catch (final MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    public void run(final ApplicationArguments args) throws Exception {
        this.process();
    }

}
