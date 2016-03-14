package org.revo.service.impl

import org.revo.domain.Admin
import org.revo.repository.AdminRepository
import org.revo.service.AdminService
import org.revo.service.SecurityService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * Created by revo on 16/01/16.
 */
@Service
class AdminServiceImpl implements AdminService {
    @Autowired
    AdminRepository adminRepository
    @Autowired
    PasswordEncoder encoder
    @Autowired
    SecurityService securityService

    @Override
    Admin findOne() {
        adminRepository.findOne(securityService.GetRevoUser().id)
    }

    @Override
    Admin findOne(Long admin) {
        adminRepository.findOne(admin)
    }

    @Transactional
    @Override
    Admin save(Admin admin) {
        admin.password = encoder.encode(admin.password)
        admin.activationKey = UUID.randomUUID().toString()
        return adminRepository.save(admin)
    }

    @Override
    boolean exist(Admin admin) {
        boolean b1 = adminRepository.findByEmail(admin.email).present
        boolean b2 = adminRepository.findByName(admin.email).present
        return (b1 || b2)
    }


    @Transactional
    @Override
    void activate(String key) {
        adminRepository.findByActivationKey(key).map {
            it.accountNonExpire = true
            it.accountNonLocked = true
            it.credentialsNonExpired = true
            it.enabled = true
            it
        }.orElseThrow {
            new Exception("not found")
        }
    }

    @Transactional
    @Override
    Admin updatePayment(int plane, Long id, boolean update) {
        if (id == securityService.GetRevoUser().id) {
            Admin admin = adminRepository.findOne(id)
            admin.plane = plane
            if (update) admin.lastPayment = new Date()
            adminRepository.save(admin)
        } else throw new Exception("error")
    }
}
