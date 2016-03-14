package org.revo.controller.Admin

import groovy.util.logging.Log
import org.revo.domain.ErrorMessage
import org.revo.domain.Student
import org.revo.service.LoggerService
import org.revo.service.StudentService
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
@RequestMapping(value = "/api/admin/student")
@Log
class A_StudentCtrl {
    @Autowired
    StudentService studentService
    @Autowired
    LoggerService loggerService
    @Autowired
    PasswordEncoder encoder

    @RequestMapping(method = RequestMethod.GET)
    def findAll() {
        try {
            Set<Student> findAll = studentService.findAll()
            findAll*.pt = null
            new ResponseEntity<>(findAll, HttpStatus.OK)
        } catch (Exception ignored) {
            loggerService.Error(log, new ErrorMessage(ErrorNumbers.A_StudentCtrl1, ignored.message, ignored.stackTrace))
        }
    }

    @RequestMapping(value = "/uploadstudents", method = RequestMethod.POST)
    def UploadStudents(@RequestParam("file") MultipartFile MFile) {
        println MFile.contentType
        if (MFile && !MFile.empty) {
            try {
                File file = new File(FileUtils.UploadFile(MFile).toString())
                List<Student> students = Util.ReadXlsxFile(file, Util.StudentClosure, encoder, 3)
                List<Student> data = studentService.save(students).toList()
                file.delete()
                new ResponseEntity<>(data, HttpStatus.OK)
            } catch (Exception ignored) {
                loggerService.Error(log, new ErrorMessage(ErrorNumbers.A_StudentCtrl2, ignored.message, ignored.stackTrace))
            }
        } else loggerService.Error(log, new ErrorMessage(ErrorNumbers.A_StudentCtrl3, "please select a xls file"))
    }

    @RequestMapping(params = "term", method = RequestMethod.GET)
    def findAllInTerm(@RequestParam("term") Long term) {
        try {
            Set<Student> findAll = studentService.findAll(term)
            findAll*.pt = null
            new ResponseEntity<>(findAll, HttpStatus.OK)
        } catch (Exception ignored) {
            loggerService.Error(log, new ErrorMessage(ErrorNumbers.A_StudentCtrl4, ignored.message, ignored.stackTrace))
        }
    }

    @RequestMapping(params = ["term", "subject"], method = RequestMethod.GET)
    def findAllInTermAndSubject(@RequestParam("term") Long term, @RequestParam("subject") Long subject) {
        try {
            new ResponseEntity<>(studentService.findAll(term, subject).collect {
                new Student(id: it[0], name: it[1], email: it[2])
            }, HttpStatus.OK)
        } catch (Exception ignored) {
            loggerService.Error(log, new ErrorMessage(ErrorNumbers.A_StudentCtrl5, ignored.message, ignored.stackTrace))
        }
    }


    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    def findOne(@PathVariable("id") Long id) {
        try {
            Student student = studentService.findOne(id)
            student.pt.each {
                it.ps*.subject*.required = null
            }
            new ResponseEntity<>(student, HttpStatus.OK);
        } catch (Exception ignored) {
            loggerService.Error(log, new ErrorMessage(ErrorNumbers.A_StudentCtrl6, ignored.message, ignored.stackTrace))
        }
    }

    @RequestMapping(method = RequestMethod.POST)
    def save(@RequestBody Student student) {
        try {
            Student one = studentService.save(student)
            one.pt = null
            new ResponseEntity<>(one, HttpStatus.OK)
        } catch (ConstraintViolationException ignored) {
            loggerService.Error(log, new ErrorMessage(ErrorNumbers.A_StudentCtrl7, ignored.constraintViolations.collect {
                it.message
            }.join(","), ignored.stackTrace))
        } catch (Exception ignored) {
            loggerService.Error(log, new ErrorMessage(ErrorNumbers.A_StudentCtrl8, ignored.message, ignored.stackTrace))
        }
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    def delete(@PathVariable("id") Long id) {
        try {
            studentService.delete(id)
            new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception ignored) {
            loggerService.Error(log, new ErrorMessage(ErrorNumbers.A_StudentCtrl9, ignored.message, ignored.stackTrace))
        }
    }

    @RequestMapping(value = "/{id}", params = "term", method = RequestMethod.DELETE)
    def deleteInTerm(@PathVariable("id") Long student, @RequestParam("term") Long term) {
        try {
            studentService.delete(student, term)
            new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception ignored) {
            loggerService.Error(log, new ErrorMessage(ErrorNumbers.A_StudentCtrl10, ignored.message, ignored.stackTrace))
        }
    }

    @RequestMapping(value = "/{id}", params = ["term", "subject"], method = RequestMethod.DELETE)
    def deleteInTermAndSubject(
            @PathVariable("id") Long student, @RequestParam("term") Long term, @RequestParam("subject") Long subject) {
        try {
            studentService.delete(student, term, subject)
            new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception ignored) {
            loggerService.Error(log, new ErrorMessage(ErrorNumbers.A_StudentCtrl11, ignored.message, ignored.stackTrace))
        }
    }
}
