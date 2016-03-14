package org.revo.controller.Admin

import groovy.util.logging.Log
import org.revo.domain.ErrorMessage
import org.revo.domain.Subject
import org.revo.service.LoggerService
import org.revo.service.SubjectService
import org.revo.service.impl.ErrorNumbers
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

import javax.validation.ConstraintViolationException

/**
 * Created by revo on 06/01/16.
 */
@RestController
@RequestMapping(value = "/api/admin/subject")
@Log
class A_SubjectCtrl {
    @Autowired
    SubjectService subjectService
    @Autowired
    LoggerService loggerService

    @RequestMapping(method = RequestMethod.GET)
    def findAll() {
        try {
            new ResponseEntity<>(subjectService.findAll(), HttpStatus.OK)
        } catch (Exception ignored) {
            loggerService.Error(log, new ErrorMessage(ErrorNumbers.A_SubjectCtrl1, ignored.message, ignored.stackTrace))
        }
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    def findOne(@PathVariable("id") Long id) {
        try {
            Subject one = subjectService.findOne(id)
            one.required*.required = null
            new ResponseEntity<>(one, HttpStatus.OK)
        } catch (Exception ignored) {
            loggerService.Error(log, new ErrorMessage(ErrorNumbers.A_SubjectCtrl2, ignored.message, ignored.stackTrace))
        }
    }

    @RequestMapping(method = RequestMethod.POST)
    def save(@RequestBody Subject subject) {
        try {
            Subject save = subjectService.save(subject)
            save.required*.required = null
            new ResponseEntity<>(save, HttpStatus.OK)
        } catch (ConstraintViolationException ignored) {
            loggerService.Error(log, new ErrorMessage(ErrorNumbers.A_SubjectCtrl3, ignored.constraintViolations.collect {
                it.message
            }.join(","), ignored.stackTrace))
        } catch (Exception ignored) {
            loggerService.Error(log, new ErrorMessage(ErrorNumbers.A_SubjectCtrl3, ignored.message, ignored.stackTrace))
        }
    }

    @RequestMapping(method = RequestMethod.PUT)
    def required(@RequestBody Subject subject) {
        try {
            new ResponseEntity<>(subjectService.required(subject), HttpStatus.OK)
        } catch (Exception ignored) {
            loggerService.Error(log, new ErrorMessage(ErrorNumbers.A_SubjectCtrl4, ignored.message, ignored.stackTrace))
        }
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    def delete(@PathVariable("id") Long id) {
        try {
            subjectService.delete(id)
            new ResponseEntity<>(HttpStatus.NO_CONTENT)
        } catch (Exception ignored) {
            loggerService.Error(log, new ErrorMessage(ErrorNumbers.A_SubjectCtrl5, ignored.message, ignored.stackTrace))
        }
    }
}
