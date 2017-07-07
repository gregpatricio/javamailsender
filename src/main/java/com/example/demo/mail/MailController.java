package com.example.demo.mail;

import freemarker.template.TemplateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.mail.MessagingException;
import java.io.IOException;

@Controller
public class MailController {

    @Autowired
    private MailService mailService;


    @RequestMapping(path="/mails")
    @ResponseStatus(HttpStatus.OK)
    public void sendMail() throws IOException, TemplateException, MessagingException {
        mailService.send();
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception e) {
        return new ResponseEntity<>("Error!!", HttpStatus.BAD_REQUEST);
    }

}
