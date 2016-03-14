package org.revo.repository;

import org.revo.domain.Student;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Created by revo on 04/12/15.
 */
public interface StudentRepository extends CrudRepository<Student, Long> {
    Optional<Student> findByEmailAndAdmin_Id(String email, Long id);

    List<Student> findByEmailInAndAdmin_Id(List<String> email, Long id);

    Set<Student> findByAdmin_Id(Long admin);

    @Query(value = "select st from Student st,PT pt where pt.student.id=st.id and pt.term.id=?1 and st.admin.id=?2 and pt.ps.size>0")
    Set<Student> findByJoinTerm(Long term, Long admin);

    @Query(value = "select st.id,st.name,st.email from Student st,PT pt,PS ps where pt.student.id=st.id and pt.term.id=?1 and st.admin.id=?3 and ps.pt.id=pt.id and ps.subject.id=?2  and pt.ps.size>0")
    List<Object[]> findByJoinTermInSubject(Long term, Long subject, Long admin);

    Student findByIdAndAdmin_Id(Long student, Long admin);

    void deleteByIdAndAdmin_Id(Long student, Long admin);

    int countByAdmin_Id(Long id);
}
