package org.revo.controller.Admin

import groovy.util.logging.Log
import org.revo.domain.ErrorMessage
import org.revo.domain.GradeStudents
import org.revo.domain.Student
import org.revo.domain.Subject
import org.revo.domain.Term
import org.revo.service.LoggerService
import org.revo.service.TermService
import org.revo.service.impl.ErrorNumbers
import org.revo.service.impl.FileUtils
import org.revo.service.impl.Util
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

import javax.validation.ConstraintViolationException

/**
 * Created by revo on 06/01/16.
 */
@RestController
@RequestMapping(value = "/api/admin/term")
@Log
class A_TermCtrl {
    @Autowired
    TermService termService
    @Autowired
    LoggerService loggerService
    @Autowired
    PasswordEncoder encoder

    @RequestMapping(method = RequestMethod.GET)
    def findAll() {
        try {
            new ResponseEntity<>(termService.findAll(), HttpStatus.OK)
        } catch (Exception ignored) {
            loggerService.Error(log, new ErrorMessage(ErrorNumbers.A_TermCtrl1, ignored.message, ignored.stackTrace))
        }
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    def findOne(@PathVariable("id") Long id) {
        try {
            new ResponseEntity<>(termService.findOne(id), HttpStatus.OK)
        } catch (Exception ignored) {
            loggerService.Error(log, new ErrorMessage(ErrorNumbers.A_TermCtrl2, ignored.message, ignored.stackTrace))
        }
    }

    @RequestMapping(value = "statics/{id}", method = RequestMethod.GET)
    def Statics(@PathVariable("id") Long id) {
        try {
            List<Objects[]> statics = termService.Statics(id)
            new ResponseEntity<>(statics.collect {
                ["subject": new Subject(id: it[0], name: it[1], maxGrade: it[2], hour: it[3]), "count": it[4]]
            }, HttpStatus.OK)
        } catch (Exception ignored) {
            loggerService.Error(log, new ErrorMessage(ErrorNumbers.A_TermCtrl2, ignored.message, ignored.stackTrace))
        }
    }

    @RequestMapping(method = RequestMethod.POST)
    def save(@RequestBody Term term) {
        try {
            Term one = termService.save(term)
            new ResponseEntity<>(one, HttpStatus.OK)
        } catch (ConstraintViolationException ignored) {
            loggerService.Error(log, new ErrorMessage(ErrorNumbers.A_TermCtrl3, ignored.constraintViolations.collect {
                it.message
            }.join(","), ignored.stackTrace))
        } catch (Exception ignored) {
            loggerService.Error(log, new ErrorMessage(ErrorNumbers.A_TermCtrl3, ignored.message, ignored.stackTrace))
        }
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    def delete(@PathVariable("id") Long term) {
        try {
            termService.delete(term)
            new ResponseEntity<>(HttpStatus.NO_CONTENT)
        } catch (Exception ignored) {
            loggerService.Error(log, new ErrorMessage(ErrorNumbers.A_TermCtrl4, ignored.message, ignored.stackTrace))
        }
    }

    @RequestMapping(value = "/uploadgrades", method = RequestMethod.POST)
    def UploadStudents(@RequestParam("file") MultipartFile MFile) {
        println MFile.contentType
        if (MFile && !MFile.empty) {
            try {
                File file = new File(FileUtils.UploadFile(MFile).toString())
                List<GradeStudents> gradeStudentses = Util.ReadXlsxFile(file, Util.GradeStudentsClosure, encoder, 5)
                file.delete()
                termService.addGrades(gradeStudentses)
                new ResponseEntity<>(HttpStatus.NO_CONTENT)
            } catch (Exception ignored) {
                loggerService.Error(log, new ErrorMessage(ErrorNumbers.A_StudentCtrl2, ignored.message, ignored.stackTrace))
            }
        } else loggerService.Error(log, new ErrorMessage(ErrorNumbers.A_StudentCtrl3, "please select a xls file"))
    }

}
