package org.revo.service

import org.revo.domain.PS
import org.revo.domain.Student
import org.revo.domain.Subject

/**
 * Created by revo on 13/01/16.
 */
interface PSService {
    Set<PS> findAll(Long term, List<Long> subjectid, List<String> studentid)

    Set<PS> findInTerm(Long term)

    PS findOne(Long ps)

    PS save(PS ps) throws Exception

    void delete(Long ps)

}