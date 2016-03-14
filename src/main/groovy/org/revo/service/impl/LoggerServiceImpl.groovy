package org.revo.service.impl

import org.revo.domain.ErrorMessage
import org.revo.service.LoggerService
import org.revo.service.SecurityService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service

import java.util.logging.Level
import java.util.logging.Logger

/**
 * Created by revo on 23/01/16.
 */
@Service
class LoggerServiceImpl implements LoggerService {
    @Autowired
    SecurityService securityService

    @Override
    def Error(Logger logger, ErrorMessage em) {
        logger.log(Level.WARNING, em.users(securityService.GetRevoUser()).toString())
        new ResponseEntity<>(em, HttpStatus.INTERNAL_SERVER_ERROR)
    }
}
