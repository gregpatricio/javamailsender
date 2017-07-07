package com.example.demo.mail;

import freemarker.template.Configuration;
import freemarker.template.TemplateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.URLDataSource;
import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.IOException;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


@Service
public class MailService {

    @Autowired
    private JavaMailSender sender;

    @Autowired
    private Configuration configuration;

    public void send() throws IOException, TemplateException, MessagingException {
        Mail mail = new Mail();
        mail.setTo("gpatricio@pivotal.io");
        mail.setSubject("Nothing relevant");

        MimeMessage message = sender.createMimeMessage();

        message.setFrom("gpatricio@pivotal.io");
        message.setRecipients(MimeMessage.RecipientType.TO, mail.getTo());
        message.setSubject(mail.getSubject());

        Date timeStamp = new Date();
        message.setSentDate(timeStamp);

        Multipart multipart = new MimeMultipart();

        //BODY PART
        BodyPart htmlPart = new MimeBodyPart();
        String imageContentId = UUID.randomUUID().toString();
        htmlPart.setContent(getContentFromTemplate(mail, imageContentId), "text/html");
        multipart.addBodyPart(htmlPart);

        //IMG PART
        BodyPart imgPart = new MimeBodyPart();
        String fileName = "robot.png";
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        if (classLoader == null) {
            classLoader = this.getClass().getClassLoader();
            if (classLoader == null) {
                System.out.println("IT IS NULL AGAIN!!!!");
            }
        }

        DataSource ds = new URLDataSource(classLoader.getResource(fileName));
        imgPart.setDataHandler(new DataHandler(ds));
        imgPart.setHeader("Content-ID", "<" + imageContentId + ">");


        multipart.addBodyPart(imgPart);
        message.setContent(multipart);

        sender.send(message);
    }

    private String getContentFromTemplate(Mail mail, String imageContentId) {
        StringBuffer content = new StringBuffer();

        try {
            content.append(FreeMarkerTemplateUtils.processTemplateIntoString(configuration.getTemplate("template.ftlh"), toMap(mail, imageContentId)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return content.toString();
    }

    private Map<String, Object> toMap(Mail mail, String imageContentId) throws IOException {
        HashMap<String, Object> data = new HashMap<>();
        data.put("userEmail", mail.getTo());
        data.put("cid", imageContentId);
        return data;
    }

}
