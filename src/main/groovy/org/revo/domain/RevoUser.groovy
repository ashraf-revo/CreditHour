package org.revo.domain

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import org.revo.service.impl.Util
import org.springframework.security.core.userdetails.User

/**
 * Created by revo on 22/01/16.
 */
@JsonIgnoreProperties(["id", "admin", "plane", "password", "authorities", "accountNonExpired", "accountNonLocked", "credentialsNonExpired", "enabled"])
class RevoUser extends User {
    Long id
    Long admin
    String email
    int plane
    List<String> roles = []

    RevoUser(Student s) {
        super(s.email, s.password, s.admin.enabled, s.admin.accountNonExpire, s.admin.credentialsNonExpired, s.admin.accountNonLocked, org.revo.service.impl.Util.getRoles(s.admin.lastPayment, "ROLE_STUDENT"))
        this.id = s.id
        this.admin = s.admin.id
        this.email = s.email
        this.plane = s.admin.plane
    }

    RevoUser(Admin a) {
        super(a.name, a.password, a.enabled, a.accountNonExpire, a.credentialsNonExpired, a.accountNonLocked, org.revo.service.impl.Util.getRoles(a.lastPayment, "ROLE_ADMIN"))
        this.id = a.id
        this.admin = this.id
        this.email = a.name
        this.plane = a.plane
    }

    List<String> getRoles() {
        return Util.getRoles(authorities)
    }
}