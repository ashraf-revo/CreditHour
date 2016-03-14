package org.revo.service

import org.revo.domain.Subject

/**
 * Created by revo on 13/01/16.
 */
interface SubjectService {
    Set<Subject> findAll()

    Set<Subject> findAll(List<Long> ids)

    Set<Subject> findAllInTerm(Long id)

    Subject findOne(Long subject)

    Subject save(Subject subject) throws Exception

    Subject required(Subject subject) throws Exception

    void delete(Long subject)

}