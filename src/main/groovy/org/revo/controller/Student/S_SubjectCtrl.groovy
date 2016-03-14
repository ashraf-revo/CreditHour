package org.revo.controller.Student

import groovy.util.logging.Log
import org.revo.domain.ErrorMessage
import org.revo.service.LoggerService
import org.revo.service.PSService
import org.revo.service.SubjectService
import org.revo.service.impl.ErrorNumbers
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController

/**
 * Created by ashraf on 12/4/2015.
 */
@RestController
@RequestMapping(value = "/api/student/subject")
@Log
class S_SubjectCtrl {
    @Autowired
    SubjectService subjectService
    @Autowired
    PSService psService
    @Autowired
    LoggerService loggerService

    @RequestMapping(method = RequestMethod.GET)
    def findAll() {
        try {
            new ResponseEntity<>(subjectService.findAll(), HttpStatus.OK)
        } catch (Exception ignored) {
            loggerService.Error(log, new ErrorMessage(ErrorNumbers.S_SubjectCtrl1, ignored.message, ignored.stackTrace))
        }
    }

    @RequestMapping("/{id}")
    def findAllInTerm(@PathVariable Long id) {
        try {
            new ResponseEntity<>(subjectService.findAllInTerm(id), HttpStatus.OK)
        } catch (Exception ignored) {
            ignored.stackTrace
            loggerService.Error(log, new ErrorMessage(ErrorNumbers.S_SubjectCtrl2, ignored.message, ignored.stackTrace))
        }
    }
}
