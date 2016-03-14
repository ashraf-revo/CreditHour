package org.revo.service.impl

import org.revo.domain.Admin
import org.revo.domain.GradeStudents
import org.revo.domain.PS
import org.revo.domain.PT
import org.revo.domain.State
import org.revo.domain.Student
import org.revo.domain.Subject
import org.revo.domain.Term
import org.revo.repository.TermRepository
import org.revo.service.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * Created by revo on 13/01/16.
 */
@Service
class TermServiceImpl implements TermService {
    @Autowired
    TermRepository termRepository
    @Autowired
    AdminService adminService
    @Autowired
    SecurityService securityService
    @Autowired
    PTService ptService
    @Autowired
    StudentService studentService
    @Autowired
    SubjectService subjectService
    @Autowired
    PSService psService

    @Override
    @Transactional(readOnly = true)
    Set<Term> findAll() {
        termRepository.findByAdmin_Id(securityService.GetRevoUser().id)
    }

    @Override
    @Transactional(readOnly = true)
    Set<Term> findAll(List<Long> ides) {
        termRepository.findByIdInAndAdmin_Id(ides, securityService.GetRevoUser().id)
    }

    @Override
    @Transactional(readOnly = true)
    Term findOne(Long term) {
        termRepository.findByIdAndAdmin_Id(term, securityService.GetRevoUser().id)
    }

    @Override
    @Transactional(readOnly = true)
    List<Object[]> Statics(Long term) {
        termRepository.Statics(term, securityService.GetRevoUser().id)
    }

    @Override
    @Transactional
    Term save(Term term) throws Exception {
        if (term.minHour > term.maxHour) throw new Exception("change min hour and max hour")
        if (!term.id) {
            term = Util.CloneObject(new Term(), term, ["id"]) as Term
            term.admin = adminService.findOne(securityService.GetRevoUser().id)
            Term save = termRepository.save(term)
            studentService.findAll().each {
                ptService.save(new PT(term: save, student: it))
            }
            save

        } else {
            Term one = termRepository.findOne(term.id)
            if (one) {
                Term termClone = Util.CloneObject(one, term, ["id"]) as Term
                one = termClone
                Admin admin = adminService.findOne(securityService.GetRevoUser().id)
                if (one.admin.id == admin.id) {
                    term.admin = admin
                    term.pt = termClone.pt
                    termRepository.save(term)
                } else throw new Exception("it not your term")
            } else throw new Exception("no term with id")
        }
    }

    @Transactional
    @Override
    void delete(Long term) {
        termRepository.deleteByIdAndAdmin_Id(term, securityService.GetRevoUser().id)
    }

    @Transactional
    @Override
    void addGrades(List<GradeStudents> gradeStudentses) {
        Set<Term> allTerm = findAll(gradeStudentses*.TERM_ID)
        if (allTerm.size() > 0) {
            allTerm.each { term ->
                Set<Student> allStudent = studentService.findAll(gradeStudentses*.STUDENT_EMAIL)
                List<String> allStudentemail = allStudent*.email
                Set<Subject> allSubject = subjectService.findAll(gradeStudentses*.SUBJECT_ID)
                List<Long> allSubjectid = allSubject*.id
                gradeStudentses = gradeStudentses.findAll {
                    it.SUBJECT_ID in allSubjectid && it.STUDENT_EMAIL in allStudentemail
                }
                Set<PS> pses = psService.findAll(term.id, gradeStudentses*.SUBJECT_ID, gradeStudentses*.STUDENT_EMAIL)
                pses.collect { out ->
                    GradeStudents ps = gradeStudentses.find {
                        it.STUDENT_EMAIL == out.pt.student.email && it.SUBJECT_ID == out.subject.id
                    }
                    out.state = ps.STATE
                    out.grade = ps.GRADE
                    if (out.state == State.non) {
                        out.grade = 0
                    } else {
                        if (out.grade > out.subject.maxGrade)
                            out.grade = out.subject.maxGrade
                        if (out.grade < 0)
                            out.grade = 0
                    }
                    out
                }

            }

        }
    }
}
