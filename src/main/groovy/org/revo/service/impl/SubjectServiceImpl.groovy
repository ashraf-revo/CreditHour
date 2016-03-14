package org.revo.service.impl

import org.revo.domain.Admin
import org.revo.domain.RevoUser
import org.revo.domain.Subject
import org.revo.repository.SubjectRepository
import org.revo.service.AdminService
import org.revo.service.PSService
import org.revo.service.SecurityService
import org.revo.service.SubjectService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * Created by revo on 13/01/16.
 */
@Service
class SubjectServiceImpl implements SubjectService {
    @Autowired
    SubjectRepository subjectRepository
    @Autowired
    AdminService adminService
    @Autowired
    PSService psService
    @Autowired
    SecurityService securityService

    @Transactional(readOnly = true)
    @Override
    Set<Subject> findAll() {
        subjectRepository.findByAdmin_Id(securityService.GetRevoUser().admin)
    }

    @Override
    Set<Subject> findAll(List<Long> ids) {
        subjectRepository.findByIdInAndAdmin_Id(ids, securityService.GetRevoUser().admin)
    }

    @Transactional(readOnly = true)
    @Override
    Set<Subject> findAllInTerm(Long id) {
        Set<Subject> findInTerm = psService.findInTerm(id).collect {
            it.subject
        }
        findAll().collect {
            if (Contains(findInTerm, it)) {
                it.selected = true
            }
            it.required = null
            it
        }
    }

    @Transactional(readOnly = true)
    @Override
    Subject findOne(Long subject) {
        subjectRepository.findByIdAndAdmin_Id(subject, securityService.GetRevoUser().admin)
    }

    @Transactional
    @Override
    Subject save(Subject subject) throws Exception {
        if (!subject.id) {
            subject = Util.CloneObject(new Subject(), subject, ["id"]) as Subject
            subject.admin = adminService.findOne(securityService.GetRevoUser().id)
            subjectRepository.save(subject)
        } else {
            Subject one = subjectRepository.findOne(subject.id)
            if (one) {
                one = Util.CloneObject(one, subject, ["id"]) as Subject
                Admin admin = adminService.findOne(securityService.GetRevoUser().id)
                if (one.admin.id == admin.id) {
                    subject.admin = admin
                    subjectRepository.save(subject)
                } else throw new Exception("it not your subject")
            } else throw new Exception("no subject with this id")
        }
    }

    @Transactional
    @Override
    Subject required(Subject subject) {
        if (subject.id) {
            Subject one = subjectRepository.findOne(subject.id)
            RevoUser revoUser = securityService.GetRevoUser()
            if (one && one.admin.id == revoUser.id) {
                subject.required = subject.required.collect {
                    findOne(it.id)
                }.findAll {
                    it.admin.id == revoUser.id
                }
                if (IsNotRequiredRecursive(one, subject)) {
                    one.required = subject.required
                    subjectRepository.save(one)
                } else throw new Exception("it may be Recursive")
            } else throw new Exception("it may be null or its not to you")
        } else throw new Exception("no id to fetch subject")
    }

    @Transactional
    @Override
    void delete(Long subject) {
        subjectRepository.deleteByIdAndAdmin_Id(subject, securityService.GetRevoUser().id)
    }

    private Set<Subject> RequiredRecursive(Subject subject) {
        if (subject.required.size() == 0) [subject]
        else {
            Set d = []
            subject.required.each {
                d.add(new Subject(id: it.id))
                d.addAll(RequiredRecursive(it))
            }
            d
        }
    }

    private boolean IsNotRequiredRecursive(Subject s1, Subject s2) {
        if (s2.required.size() == 0) return true
        else
            !RequiredRecursive(s2).any {
                it.id == s1.id
            }
    }

    private boolean Contains(Set<Subject> data, Subject one) {
        data.any {
            it.id == one.id
        }
    }

}
