package org.revo.service

import org.revo.domain.ErrorMessage

import java.util.logging.Logger

/**
 * Created by revo on 23/01/16.
 */
interface LoggerService {
    def Error(Logger logger, ErrorMessage em)
}