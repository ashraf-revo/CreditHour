package org.revo.service

import org.revo.domain.GradeStudents
import org.revo.domain.Term

/**
 * Created by revo on 13/01/16.
 */
interface TermService {
    Set<Term> findAll()

    Set<Term> findAll(List<Long> ides)

    Term findOne(Long term)

    List<Object[]> Statics(Long term)

    Term save(Term term) throws Exception

    void delete(Long term)

    void addGrades(List<GradeStudents> gradeStudentses)
}