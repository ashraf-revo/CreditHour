package org.revo.service.impl

import org.revo.domain.PS
import org.revo.domain.PT
import org.revo.domain.State
import org.revo.domain.Subject
import org.revo.repository.PSRepository
import org.revo.repository.PTRepository
import org.revo.service.PSService
import org.revo.service.PTService
import org.revo.service.SecurityService
import org.revo.service.SubjectService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * Created by revo on 13/01/16.
 */
@Service
class PTServiceImpl implements PTService {
    @Autowired
    PTRepository ptRepository
    @Autowired
    SubjectService subjectService
    @Autowired
    PSService psService
    @Autowired
    PSRepository psRepository
    @Autowired
    SecurityService securityService

    @Transactional(readOnly = true)
    @Override
    Set<PT> findAll() {
        ptRepository.findByStudent_Id(securityService.GetRevoUser().id)
    }

    @Transactional(readOnly = true)
    @Override
    PT findOne(Long id) {
        Set<Subject> success = []
        Set<Subject> suggested = []
        Set<PT> one = findAll()
        one.each { x ->
            x.ps.each {
                if (it.state == State.success && x.id != id) success << it.subject
                if (it.state != State.success) {
                    if (x.id == id) it.subject.selected = true
                    suggested << it.subject
                }
            }
        }
        suggested.removeAll {
            success.any { x ->
                it.id == x.id
            }
        }
        Set<Subject> g = subjectService.findAll().findAll {
            boolean bool = true
            for (Subject subject in it.required) {
                if (!success.any { q -> q.id == subject.id }) {
                    bool = false
                    break
                }
            }
            if (it.required.size() == 0 && !success.any { a ->
                a.id == it.id
            } || (bool && !success.any { o -> o.id == it.id })) true
        }
        suggested.addAll(g)
        PT onePt = one.find {
            id == it.id
        }
        onePt.ps = new HashSet<>()
        suggested.each {
            onePt.ps.add(new PS(subject: it))
        }
        onePt
    }

    @Transactional
    @Override
    PT save(PT pt) throws Exception {
        if (!pt.id) {
            ptRepository.save(pt)
        } else {
            PT one = ptRepository.findOne(pt.id)
            int maxHour = one.term.maxHour
            int minHour = one.term.minHour

            if (one) {
                if (one.term.enabled) {
                    pt.ps = pt.ps.collect {
                        it.subject = subjectService.findOne(it.subject.id)
                        it
                    }
                    if (pt.ps.every { it.subject.admin.id == securityService.GetRevoUser().admin }) {
                        //need to check if the subject can be selected based last terms and grade of student

                        int sum = pt.ps.collect {
                            it.subject.hour
                        }.inject(0) {
                            x, i ->
                                x + i
                        }
                        if (minHour <= sum && sum <= maxHour) {
                            one.ps.findAll {
                                !Contains(pt.ps*.subject.toSet(), it.subject)
                            }.each {
                                psRepository.delete(it.id)
                            }
                            pt.ps.findAll {
                                !Contains(one.ps*.subject.toSet(), it.subject)
                            }.each {
                                one.ps << psService.save(new PS(pt: one, subject: it.subject))
                            }
                            one
                        } else throw new Exception("please select correct subject between " + minHour + " " + maxHour)
                    } else throw new Exception("it not your subjects to choose")
                } else throw new Exception("term is disabled")
            } else throw new Exception("no pt with this id")
        }
    }

    private boolean Contains(Set<Subject> data, Subject one) {
        data.any {
            it.id == one.id
        }
    }
}
