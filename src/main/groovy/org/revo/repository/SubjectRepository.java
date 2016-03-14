package org.revo.repository;

import org.revo.domain.Subject;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Set;

/**
 * Created by revo on 04/12/15.
 */
public interface SubjectRepository extends CrudRepository<Subject, Long> {
    @Query("select s from Subject s left join fetch s.required where s.admin.id=?1")
    Set<Subject> findByAdmin_Id(Long subject);

    Subject findByIdAndAdmin_Id(Long subject, Long admin);

    void deleteByIdAndAdmin_Id(Long subject, Long admin);

    Set<Subject> findByIdInAndAdmin_Id(List<Long> ids, Long id);
}
