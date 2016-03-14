package org.revo.service.impl;

import org.revo.domain.Admin;
import org.revo.domain.RevoUser;
import org.revo.domain.Student;
import org.revo.repository.AdminRepository;
import org.revo.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Created by revo on 2/5/16.
 */
@Service
@Transactional(readOnly = true)
public class UserDetailsServices implements UserDetailsService {
    @Autowired
    StudentRepository studentRepository;
    @Autowired
    AdminRepository adminRepository;
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        RevoUser revoUser = null;
        boolean found = false;
        if (!email.contains("@")) {
            Optional<Admin> admin = adminRepository.findByName(email);
            if (admin.isPresent()) {
                found = true;
                revoUser = new RevoUser(admin.get());
            }
        } else if (email.split("@").length == 2) {
            Optional<Admin> admin = adminRepository.findByName(email.split("@")[1]);
            if (admin.isPresent()) {
                Optional<Student> student = studentRepository.findByEmailAndAdmin_Id(email.split("@")[0], admin.get().getId());
                if (student.isPresent()) {
                    found = true;
                    revoUser = new RevoUser(student.map(j -> {
                        j.setEmail(email);
                        return j;
                    }).get());
                }
            }
        }
        if (!found) throw new UsernameNotFoundException("not found ");
        else return revoUser;
    }
}
