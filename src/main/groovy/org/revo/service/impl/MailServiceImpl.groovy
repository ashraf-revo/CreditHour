package org.revo.service.impl

import org.revo.domain.Admin
import org.revo.service.MailService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Service
import org.thymeleaf.context.Context
import org.thymeleaf.spring4.SpringTemplateEngine

import javax.mail.internet.MimeMessage

/**
 * Created by ashraf on 1/27/2016.
 */
@Service
class MailServiceImpl implements MailService {
    @Autowired
    private JavaMailSender javaMailSender;
    @Autowired
    private SpringTemplateEngine templateEngine;

    @Override
    void Send(String to, String subject, String text, boolean isHtml) {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, isHtml);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(text, true);
        javaMailSender.send(message);
    }

    @Override
    void SendActivation(Admin admin, String BaseUrl) {
        Context context = new Context();
        context.setVariable("name", admin.name)
        context.setVariable("url", BaseUrl + "/#/activate?key=" + admin.activationKey)
        Send(admin.email, "Activation Message", templateEngine.process("activationEmail", context), true)
    }
}
