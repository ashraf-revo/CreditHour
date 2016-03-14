package org.revo.repository;

import org.revo.domain.Term;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Set;

/**
 * Created by revo on 04/12/15.
 */
public interface TermRepository extends CrudRepository<Term, Long> {

    void deleteByIdAndAdmin_Id(Long term, Long admin);

    Term findByIdAndAdmin_Id(Long term, Long admin);

    Set<Term> findByAdmin_Id(Long id);

    Set<Term> findByIdInAndAdmin_Id(List<Long> ides, Long id);

    @Query(value = "select ps.subject.id,ps.subject.name,ps.subject.maxGrade,ps.subject.hour,count (ps.subject)from Student st,PT pt,PS ps where pt.student.id=st.id and pt.term.id=?1 and st.admin.id=?2 and pt.ps.size>0 and ps.pt.id=pt.id group by ps.subject order by count (ps.subject)")
    List<Object[]> Statics(Long term, Long admin);

}
