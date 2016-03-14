package org.revo.controller.Admin

import groovy.util.logging.Log
import org.revo.domain.ErrorMessage
import org.revo.domain.PS
import org.revo.service.LoggerService
import org.revo.service.PSService
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
@RequestMapping(value = "/api/admin/ps")
@Log
class A_PSCtrl {
    @Autowired
    PSService psService
    @Autowired
    LoggerService loggerService

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    def findOne(@PathVariable("id") Long id) {
        try {
            new ResponseEntity<>(psService.findOne(id), HttpStatus.OK)
        }
        catch (Exception ignored) {
            loggerService.Error(log, new ErrorMessage(ErrorNumbers.A_PSCtrl1, ignored.message, ignored.stackTrace))
        }
    }

    @RequestMapping(method = RequestMethod.POST)
    def save(@RequestBody PS ps) {
        try {
            ps = psService.save(ps)
            ps.subject.required = null
            new ResponseEntity<>(ps, HttpStatus.OK)
        } catch (ConstraintViolationException ignored) {
            loggerService.Error(log, new ErrorMessage(ErrorNumbers.A_PSCtrl2, ignored.constraintViolations.collect {
                it.message
            }.join(","), ignored.stackTrace))
        }
        catch (Exception ignored) {
            loggerService.Error(log, new ErrorMessage(ErrorNumbers.A_PSCtrl2, ignored.message, ignored.stackTrace))
        }
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    def delete(@PathVariable("id") Long id) {
        try {
            psService.delete(id)
            new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        catch (Exception ignored) {
            loggerService.Error(log, new ErrorMessage(ErrorNumbers.A_PSCtrl3, ignored.message, ignored.stackTrace))
        }
    }
}
