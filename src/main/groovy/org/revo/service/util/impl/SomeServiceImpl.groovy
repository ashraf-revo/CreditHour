package org.revo.service.util.impl

import org.revo.domain.*
import org.revo.repository.*
import org.revo.service.util.SomeService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * Created by revo on 04/12/15.
 */
@Service
@Transactional
class SomeServiceImpl implements SomeService {
    @Autowired
    SubjectRepository subjectRepository
    @Autowired
    StudentRepository studentRepository
    @Autowired
    TermRepository termRepository
    @Autowired
    PTRepository ptRepository
    @Autowired
    PSRepository psRepository
    @Autowired
    AdminRepository adminRepository

    @Override
    void init() {
        Admin admin = adminRepository.save(new Admin( plane: 1, name: "ashraf", email: "ashraf1abdelrasool@gmail.com", password: new BCryptPasswordEncoder().encode("01120266849"), accountNonExpire: true, accountNonLocked: true, enabled: true, credentialsNonExpired: true))
        Subject computer_language = subjectRepository.save(new Subject(name: "computer language", hour: 3, admin: admin))
        Subject algorethim = subjectRepository.save(new Subject(name: "algorethim", hour: 3, admin: admin, required: [subjectRepository.findOne(computer_language.id)]))
        Subject datasturctre = subjectRepository.save(new Subject(name: "data sturctre", hour: 3, admin: admin, required: [subjectRepository.findOne(computer_language.id)]))
        Subject filesturctre = subjectRepository.save(new Subject(name: "file sturctre", hour: 3, admin: admin, required: [subjectRepository.findOne(datasturctre.id)]))
        Subject database1 = subjectRepository.save(new Subject(name: "database1", hour: 3, admin: admin, required: [subjectRepository.findOne(filesturctre.id)]))
        Subject arbic = subjectRepository.save(new Subject(name: "arbic", hour: 3, admin: admin))
        Subject english = subjectRepository.save(new Subject(name: "english", hour: 3, admin: admin))
        Subject math1 = subjectRepository.save(new Subject(name: "math1", hour: 3, admin: admin))
        Subject math2 = subjectRepository.save(new Subject(name: "math2", hour: 3, admin: admin, required: [subjectRepository.findOne(math1.id)]))
        Subject static1 = subjectRepository.save(new Subject(name: "static1", hour: 3, admin: admin, required: [subjectRepository.findOne(math1.id)]))
        Subject static2 = subjectRepository.save(new Subject(name: "static2", hour: 3, admin: admin, required: [subjectRepository.findOne(static1.id)]))
        Subject physics = subjectRepository.save(new Subject(name: "physics", hour: 3, admin: admin))
        Subject prefrals = subjectRepository.save(new Subject(name: "prefrals", hour: 3, admin: admin, required: [subjectRepository.findOne(computer_language.id)]))
        Subject java = subjectRepository.save(new Subject(name: "java", hour: 3, admin: admin, required: [subjectRepository.findOne(algorethim.id)]))
        Subject ai = subjectRepository.save(new Subject(name: "ai", admin: admin, hour: 3, required: [subjectRepository.findOne(datasturctre.id), subjectRepository.findOne(java.id)]))
        Subject python = subjectRepository.save(new Subject(name: "python", admin: admin, hour: 3, required: [subjectRepository.findOne(computer_language.id), subjectRepository.findOne(ai.id)]))
        Subject os1 = subjectRepository.save(new Subject(name: "os1", admin: admin, hour: 3))
        Subject software1 = subjectRepository.save(new Subject(name: "software1", admin: admin, hour: 3))
        Subject software2 = subjectRepository.save(new Subject(name: "software2", admin: admin, hour: 3, required: [subjectRepository.findOne(software1.id), subjectRepository.findOne(java.id)]))
        Subject descreat = subjectRepository.save(new Subject(name: "descreat", admin: admin, hour: 3))
        Subject logic1 = subjectRepository.save(new Subject(name: "logic1", admin: admin, hour: 3, required: [subjectRepository.findOne(descreat.id)]))
        Subject logic2 = subjectRepository.save(new Subject(name: "logic2", admin: admin, hour: 3, required: [subjectRepository.findOne(logic1.id)]))
        Subject assamply = subjectRepository.save(new Subject(name: "assamply", admin: admin, hour: 3, required: [subjectRepository.findOne(logic2.id), subjectRepository.findOne(computer_language.id)]))
        Subject os2 = subjectRepository.save(new Subject(name: "os2", hour: 3, admin: admin, required: [subjectRepository.findOne(os1.id), subjectRepository.findOne(assamply.id)]))
        Subject security = subjectRepository.save(new Subject(name: "security", admin: admin, hour: 3, required: [subjectRepository.findOne(java.id)]))
        Subject graphic = subjectRepository.save(new Subject(name: "graphic", admin: admin, hour: 3))
        Subject multimedia = subjectRepository.save(new Subject(name: "multimedia", admin: admin, hour: 3, required: [subjectRepository.findOne(graphic.id)]))
        Subject compiler = subjectRepository.save(new Subject(name: "compiler", admin: admin, hour: 3, required: [subjectRepository.findOne(os2.id)]))
        Student one = studentRepository.save(new Student(name: "revo", admin: admin, email: "revoxa", password: new BCryptPasswordEncoder().encode("revo")))
        Term save = termRepository.save(new Term(name: "term1", enabled: true, admin: admin))
        termRepository.save(new Term(name: "term2", enabled: true, admin: admin))
        Set<Subject> join = [
                subjectRepository.findOne(computer_language.id), subjectRepository.findOne(arbic.id),
                subjectRepository.findOne(english.id), subjectRepository.findOne(math1.id),
                subjectRepository.findOne(prefrals.id), subjectRepository.findOne(software1.id),
        ]
        joinTerm(one.id, save.id, join)
    }

    @Override
    void joinTerm(Long student, Long term, Set<Subject> subject) {
        Term one = termRepository.findOne(term)
        if (one.enabled) {
            PT pt = ptRepository.save(new PT(term: one,
                    student: studentRepository.findOne(student)))
            subject.each {
                psRepository.save(new PS(subject: it, pt: pt))
            }
        }

    }

    @Override
    Long hours(Student p) {
        Long sum = 0
        p.pt.each {
            it.ps.each { w ->
                if (w.state == State.success) sum += w.subject.hour
            }
        }
        sum
    }

}
