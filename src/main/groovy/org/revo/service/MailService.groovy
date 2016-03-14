package org.revo.service

import org.revo.domain.Admin
import org.springframework.scheduling.annotation.Async

/**
 * Created by ashraf on 1/27/2016.
 */
interface MailService {
    @Async
    void Send(String to, String subject, String text, boolean isHtml)
    @Async
    void SendActivation(Admin admin, String BaseUrl)

}