package org.revo.service.impl

import org.revo.domain.Admin
import org.revo.domain.Student
import org.revo.repository.PSRepository
import org.revo.repository.PTRepository
import org.revo.repository.StudentRepository
import org.revo.service.AdminService
import org.revo.service.PSService
import org.revo.service.SecurityService
import org.revo.service.StudentService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.env.Environment
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * Created by revo on 13/01/16.
 */
@Service
class StudentServiceImpl implements StudentService {
    @Autowired
    StudentRepository studentRepository
    @Autowired
    AdminService adminService
    @Autowired
    PTRepository ptRepository
    @Autowired
    PSRepository psRepository
    @Autowired
    SecurityService securityService
    @Autowired
    PasswordEncoder encoder
    @Autowired
    Environment env

    @Transactional(readOnly = true)
    @Override
    Set<Student> findAll() {
        studentRepository.findByAdmin_Id(securityService.GetRevoUser().id)
    }

    @Transactional(readOnly = true)
    @Override
    Set<Student> findAll(Long term) {
        studentRepository.findByJoinTerm(term, securityService.GetRevoUser().id)
    }

    @Override
    Set<Student> findAll(List<String> ids) {
        studentRepository.findByEmailInAndAdmin_Id(ids, securityService.GetRevoUser().id).toSet()
    }

    @Transactional(readOnly = true)
    @Override
    List<Object[]> findAll(Long term, Long subject) {
        studentRepository.findByJoinTermInSubject(term, subject, securityService.GetRevoUser().id)
    }

    @Transactional(readOnly = true)
    @Override
    Student findOne(Long student) {
        studentRepository.findByIdAndAdmin_Id(student, securityService.GetRevoUser().admin)
    }

    @Transactional(readOnly = true)
    @Override
    Optional<Student> findOne(String student) {
        studentRepository.findByEmailAndAdmin_Id(student, securityService.GetRevoUser().admin)
    }

    @Transactional
    @Override
    Student save(Student student) throws Exception {
        if (!student.id) {
            if (!canSave()) throw new Exception("to save this one scale your plane")
            if (findOne(student.email).isPresent()) throw new Exception("please change this email by selecting another one")
            student = Util.CloneObject(new Student(), student, ["id"]) as Student
            student.admin = adminService.findOne(securityService.GetRevoUser().id)
            student.password = encoder.encode(student.password)
            studentRepository.save(student)
        } else {
            Student one = studentRepository.findOne(student.id)
            if (one) {
                def ig = ["id", "email"];
                if (student.password == null || student.password.trim().size() == 0) {
                    student.password = one.password
                    ig << "password"
                } else
                    student.password = encoder.encode(student.password)
                Student studentClone = Util.CloneObject(one, student, ig) as Student
                one = studentClone
                Admin admin = adminService.findOne(securityService.GetRevoUser().id)
                if (one.admin.id == admin.id) {
                    student.admin = admin
                    student.pt = studentClone.pt
                    studentRepository.save(student)
                } else throw new Exception("it not your student")
            } else throw new Exception("no student with this id")
        }

    }


    @Transactional
    @Override
    Iterator<Student> save(List<Student> students) throws Exception {
        students = students.toUnique { a, b ->
            a.email <=> b.email
        }
        List<Student> founded = studentRepository.findByEmailInAndAdmin_Id(students*.email, securityService.GetRevoUser().id)
        if (founded.size() > 0)
            students.removeAll {
                it.email in founded*.email
            }
        Admin one = adminService.findOne()
        List<Student> take = students.take(max() - count()).collect {
            it.admin = one
            it
        }
        studentRepository.save(take).iterator()
    }

    @Transactional
    @Override
    void delete(Long student) {
        studentRepository.deleteByIdAndAdmin_Id(student, securityService.GetRevoUser().id)

    }

    @Transactional
    @Override
    void delete(Long student, Long term) {
        Long admin = securityService.GetRevoUser().id
        psRepository.deleteByPt_Term_IdAndPt_Student_IdAndPt_Term_Admin_IdAndPt_Student_Admin_Id(term, student, admin, admin)
    }

    @Transactional
    @Override
    void delete(Long student, Long term, Long subject) {
        Long admin = securityService.GetRevoUser().id
        psRepository.deleteBySubject_IdAndSubject_Admin_IdAndPt_Term_IdAndPt_Term_Admin_IdAndPt_Student_IdAndPt_Student_Admin_Id(subject, admin, term, admin, student, admin)
    }

    @Override
    boolean canSave() {
        max() > count()
    }

    @Override
    int max() {
        (securityService.GetRevoUser().plane * env.getProperty("rate").toInteger())
    }

    @Override
    int count() {
        studentRepository.countByAdmin_Id(securityService.GetRevoUser().id)
    }
}
