package org.revo.repository;

import org.revo.domain.Admin;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

/**
 * Created by ashraf on 12/4/2015.
 */
public interface AdminRepository extends CrudRepository<Admin, Long> {
    Optional<Admin> findByEmail(String email);

    Optional<Admin> findByName(String name);

    Optional<Admin> findByActivationKey(String ActivationKey);
}
