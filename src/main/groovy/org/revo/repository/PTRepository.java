package org.revo.repository;

import org.revo.domain.PT;
import org.springframework.data.repository.CrudRepository;

import java.util.Set;

/**
 * Created by revo on 04/12/15.
 */
public interface PTRepository extends CrudRepository<PT, Long> {
    Set<PT> findByStudent_Id(Long id);
}
