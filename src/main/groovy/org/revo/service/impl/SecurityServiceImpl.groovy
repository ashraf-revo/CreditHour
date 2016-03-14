package org.revo.service.impl

import org.revo.domain.RevoUser
import org.revo.service.SecurityService
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service

/**
 * Created by revo on 05/12/15.
 */
@Service
class SecurityServiceImpl implements SecurityService {
    RevoUser GetRevoUser() throws Exception {
        Authentication authentication = SecurityContextHolder.context.authentication
        if (authentication != null) {
            if (authentication.getPrincipal() instanceof RevoUser) {
                return authentication.principal as RevoUser
            } else {
                throw new Exception("not login user")
            }
        } else throw new Exception("not login user")
    }
}