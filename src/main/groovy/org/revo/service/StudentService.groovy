package org.revo.service

import org.revo.domain.Student
import org.springframework.transaction.annotation.Transactional

/**
 * Created by revo on 13/01/16.
 */
interface StudentService {
    Set<Student> findAll()

    Set<Student> findAll(Long term)

    Set<Student> findAll(List<String> ids)

    List<Object[]> findAll(Long term, Long subject)

    Student findOne(Long student)

    Optional<Student> findOne(String student)

    Student save(Student student) throws Exception

    Iterator<Student> save(List<Student> students) throws Exception

    void delete(Long student)

    void delete(Long student, Long term)

    void delete(Long student, Long term, Long subject)

    boolean canSave()

    int max()

    int count()
}