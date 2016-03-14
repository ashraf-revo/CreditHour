package org.revo.controller

import org.revo.domain.Admin
import org.revo.domain.ErrorMessage
import org.revo.service.AdminService
import org.revo.service.MailService
import org.revo.service.SecurityService
import org.revo.service.impl.ErrorNumbers
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*

import javax.servlet.http.HttpServletRequest
import javax.validation.ConstraintViolationException
import javax.validation.Valid

/**
 * Created by ashraf on 12/4/2015.
 */
@RestController
@RequestMapping(value = "/api")
class MainCtrl {
    @Autowired
    SecurityService securityService
    @Autowired
    AdminService adminService
    @Autowired
    MailService mailService

    @RequestMapping(value = "account", method = RequestMethod.GET)
    def getAccount() {
        try {
            new ResponseEntity<>(securityService.GetRevoUser(), HttpStatus.OK)
        } catch (Exception ignored) {
            new ResponseEntity<>(HttpStatus.UNAUTHORIZED)
        }
    }

    @RequestMapping(value = "register", method = RequestMethod.POST)
    def register(@Valid @RequestBody Admin admin, BindingResult result, HttpServletRequest request) {
        try {
            if (result.hasErrors()) {
                throw new Exception(result.getFieldErrors().collect {
                    it.field + "   " + it.defaultMessage
                }.join("  ,   "))
            }
            if (adminService.exist(admin)) throw new Exception("please change your name or email")
            Admin save = adminService.save(admin)
            String baseUrl = request.scheme + "://" + request.serverName + ":" + request.serverPort + request.contextPath
            mailService.SendActivation(save, baseUrl)
            new ResponseEntity<>(HttpStatus.NO_CONTENT)
        } catch (ConstraintViolationException ignored) {
            new ResponseEntity<>(new ErrorMessage(ErrorNumbers.A_StudentCtrl4, ignored.constraintViolations.collect {
                it.message
            }.join(",")), HttpStatus.INTERNAL_SERVER_ERROR)
        }
        catch (Exception ignored) {
            new ResponseEntity<>(new ErrorMessage(ignored.message), HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }

    @RequestMapping(value = "activate", method = RequestMethod.GET)
    def activate(@RequestParam(value = "key") String key) {
        try {
            adminService.activate(key)
            new ResponseEntity<>(HttpStatus.NO_CONTENT)
        }
        catch (Exception ignored) {
            new ResponseEntity<>(new ErrorMessage(ignored.message), HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }
}
