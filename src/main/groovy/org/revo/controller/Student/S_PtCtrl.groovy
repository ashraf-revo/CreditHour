package org.revo.controller.Student

import groovy.util.logging.Log
import org.revo.domain.ErrorMessage
import org.revo.domain.PT
import org.revo.service.LoggerService
import org.revo.service.PTService
import org.revo.service.impl.ErrorNumbers
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

/**
 * Created by ashraf on 12/4/2015.
 */
@RestController
@RequestMapping(value = "/api/student/pt")
@Log
class S_PtCtrl {
    @Autowired
    PTService ptService
    @Autowired
    LoggerService loggerService

    @RequestMapping(method = RequestMethod.GET)
    def findAll() {
        try {
            Set<PT> findAll = ptService.findAll()
            findAll*.ps = null
            new ResponseEntity<>(findAll, HttpStatus.OK)
        } catch (Exception ignored) {
            loggerService.Error(log, new ErrorMessage(ErrorNumbers.S_PtCtrl1, ignored.message, ignored.stackTrace))
        }
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    def findOneInAll(@PathVariable Long id) {
        try {
            new ResponseEntity<>(ptService.findOne(id), HttpStatus.OK)
        } catch (Exception ignored) {
            loggerService.Error(log, new ErrorMessage(ErrorNumbers.S_PtCtrl2, ignored.message, ignored.stackTrace))
        }
    }

    @RequestMapping(method = RequestMethod.POST)
    def save(@RequestBody PT pt) {
        try {
            ptService.save(pt)
            new ResponseEntity<>(HttpStatus.NO_CONTENT)
        } catch (Exception ignored) {
            loggerService.Error(log, new ErrorMessage(ErrorNumbers.S_PtCtrl3, ignored.message, ignored.stackTrace))
        }
    }
}
