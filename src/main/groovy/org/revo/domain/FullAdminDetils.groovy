package org.revo.domain

import org.springframework.core.env.Environment

/**
 * Created by ashraf on 2/16/2016.
 */
class FullAdminDetils extends Admin {
    int studentCount
    int rate
    int cost

    FullAdminDetils() {
    }

    FullAdminDetils(Admin admin, int studentCount, Environment env) {
        this.id = admin.id
        this.name = admin.name
        this.email = admin.email
        this.plane = admin.plane
        this.createdDate = admin.createdDate
        this.lastPayment = admin.lastPayment
        this.enabled = admin.enabled
        this.accountNonExpire = admin.accountNonExpire
        this.accountNonLocked = admin.accountNonLocked
        this.credentialsNonExpired = admin.credentialsNonExpired
        this.studentCount = studentCount
        rate = Integer.valueOf(env.getProperty("rate"))
        cost = Integer.valueOf(env.getProperty("cost"))
    }
}
