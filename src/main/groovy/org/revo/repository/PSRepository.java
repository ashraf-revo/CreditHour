package org.revo.repository;

import org.revo.domain.PS;
import org.revo.domain.Student;
import org.revo.domain.Subject;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Set;

/**
 * Created by revo on 04/12/15.
 */
public interface PSRepository extends CrudRepository<PS, Long> {
    Set<PS> findByPt_Student_IdAndPt_Term_Id(Long Student, Long term);

    @Query("select ps from PS ps left join fetch ps.pt pt left join pt.student where ps.pt.term.id=?1 and ps.subject.id in ?2 and ps.pt.student.email in ?3")
    Set<PS> findByPt_Term_IdAndSubject_IdInAndPt_Student_EmailIn(Long term, List<Long> subjectid, List<String> emails);

    PS findByIdAndPt_Term_Admin_Id(Long ps, Long admin);

    void deleteByIdAndPt_Term_Admin_Id(Long ps, Long admin);

    void deleteBySubject_IdAndSubject_Admin_IdAndPt_Term_IdAndPt_Term_Admin_IdAndPt_Student_IdAndPt_Student_Admin_Id(Long subject, Long admin1, Long term, Long admin2, Long student, Long admin3);

    void deleteByPt_Term_IdAndPt_Student_IdAndPt_Term_Admin_IdAndPt_Student_Admin_Id(Long l1, Long l2, Long l3, Long l4);
}
