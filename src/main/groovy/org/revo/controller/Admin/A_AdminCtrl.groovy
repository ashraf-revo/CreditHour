package org.revo.controller.Admin

import com.paypal.api.payments.*
import groovy.util.logging.Log
import org.revo.domain.ErrorMessage
import org.revo.domain.FullAdminDetils
import org.revo.domain.PaypalUser
import org.revo.service.AdminService
import org.revo.service.LoggerService
import org.revo.service.PaypalService
import org.revo.service.StudentService
import org.revo.service.impl.ErrorNumbers
import org.revo.service.impl.Util
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.env.Environment
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.ResponseBody

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * Created by ashraf on 2/16/2016.
 */
@Controller
@RequestMapping(value = "/api/admin/admin")
@Log
class A_AdminCtrl {

    @Autowired
    LoggerService loggerService
    @Autowired
    AdminService adminService
    @Autowired
    StudentService studentService
    @Autowired
    Environment env
    @Autowired
    PaypalService paypalService

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    def findOne() {
        try {
            new ResponseEntity<>(new FullAdminDetils(adminService.findOne(), studentService.count(), env), HttpStatus.OK)
        }
        catch (Exception ignored) {
            loggerService.Error(log, new ErrorMessage(ErrorNumbers.A_AdminCtrl1, ignored.message, ignored.stackTrace))
        }
    }

    @RequestMapping(value = "scale", method = RequestMethod.POST)
    @ResponseBody
    def scale(@RequestBody FullAdminDetils fullAdminDetils, HttpServletRequest request) {
        try {
            FullAdminDetils detils = new FullAdminDetils(adminService.findOne(), studentService.count(), env)
            if (!(new Date().time > Util.AddToDate(detils.lastPayment, Calendar.YEAR, 1).time)) {
                if (detils.plane != fullAdminDetils.plane && fullAdminDetils.plane >= Math.ceil(detils.studentCount / detils.rate)) {
                    if (fullAdminDetils.plane < detils.plane) {
                        adminService.updatePayment(fullAdminDetils.plane, detils.id, false)
                        new ResponseEntity<>(HttpStatus.OK)
                    } else {
                        Payment payment = Util.DefaultPayment("Scale", fullAdminDetils.plane - detils.plane, detils.cost, "USD", 1, 0, 1)
                        payment.setRedirectUrls(Util.GETRedirectUrls(request, String.valueOf(detils.id), env))
                        Payment create = payment.create(paypalService.GetAPIContext())
                        new ResponseEntity<>(create, HttpStatus.OK)
                    }
                } else throw new Exception("some error with your new plane please select anathor one")
            } else {
                throw new Exception("you should pay for new year not scale ")
            }
        }
        catch (Exception ignored) {
            loggerService.Error(log, new ErrorMessage(ErrorNumbers.A_AdminCtrl2, ignored.message, ignored.stackTrace))
        }
    }

    @RequestMapping(value = "pay", method = RequestMethod.POST)
    @ResponseBody
    def pay(@RequestBody FullAdminDetils fullAdminDetils, HttpServletRequest request) {
        try {
            FullAdminDetils detils = new FullAdminDetils(adminService.findOne(), studentService.count(), env)
            fullAdminDetils.plane = Math.max(Math.ceil(detils.studentCount / detils.rate), fullAdminDetils.plane)
            Payment payment = Util.DefaultPayment("Pay", fullAdminDetils.plane, detils.cost, "USD", 1, 0, 1)
            String id = String.valueOf(detils.id)
            payment.setRedirectUrls(Util.GETRedirectUrls(request, id, env))
            Payment create = payment.create(paypalService.GetAPIContext())
            new ResponseEntity<>(create, HttpStatus.OK)
        }
        catch (Exception ignored) {
            loggerService.Error(log, new ErrorMessage(ErrorNumbers.A_AdminCtrl3, ignored.message, ignored.stackTrace))
        }
    }

    @RequestMapping("fail")
    def fail(PaypalUser paypalUser) {
        "redirect:" + env.getProperty("redirectUrl")
    }

    @RequestMapping("success")
    def success(PaypalUser paypalUser, HttpServletRequest req, HttpServletResponse res) {
        try {
            if (paypalUser.paymentId) {
                FullAdminDetils detils = new FullAdminDetils(adminService.findOne(), studentService.count(), env)
                Payment payment = new Payment();
                payment.setId(paypalUser.paymentId);
                PaymentExecution paymentExecution = new PaymentExecution();
                paymentExecution.setPayerId(paypalUser.payerID);
                payment = Payment.get(paypalService.GetAPIContext().accessToken, paypalUser.paymentId);
                Payment p = payment.execute(paypalService.GetAPIContext().accessToken, paymentExecution);
                List<Transaction> transactions = p.getTransactions()
                if (transactions.size() > 0) {
                    ItemList itemList = transactions.get(0).itemList
                    List<Item> itemses = itemList.items
                    if (itemses.size() > 0) {
                        int plane = Integer.valueOf(itemses.get(0).quantity)
                        if (itemses.get(0).name.equals("Pay") && String.valueOf(((plane * detils.cost) + 1 + 1)) == transactions.get(0).amount.total.replace(".00", "")) {
                            adminService.updatePayment(Math.max(Math.ceil(detils.studentCount / detils.rate), plane).toInteger(), Integer.valueOf(paypalUser.uuid), true)
                        } else if (itemses.get(0).name.equals("Scale") && !(new Date().time > Util.AddToDate(detils.lastPayment, Calendar.YEAR, 1).time) && String.valueOf((plane * detils.cost) + 1 + 1) == transactions.get(0).amount.total.replace(".00", "")) {
                            adminService.updatePayment(plane + detils.plane, Integer.valueOf(paypalUser.uuid), false)
                        } else loggerService.Error(log, new ErrorMessage(ErrorNumbers.A_AdminCtrl4, "error  please check it "))
                        logout(req, res)
                    }
                }
            }
        }
        catch (Exception ignored) {
            loggerService.Error(log, new ErrorMessage(ErrorNumbers.A_AdminCtrl5, ignored.message, ignored.stackTrace))
        }
        "redirect:" + env.getProperty("redirectUrl")
    }

    private void logout(HttpServletRequest req, HttpServletResponse res) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        new SecurityContextLogoutHandler().logout(req, res, auth);
    }
}
