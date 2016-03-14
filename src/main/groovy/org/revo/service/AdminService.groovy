package org.revo.service

import org.revo.domain.Admin

/**
 * Created by revo on 16/01/16.
 */
interface AdminService {
    Admin findOne()

    Admin findOne(Long admin)

    Admin save(Admin admin)

    boolean exist(Admin admin)

    void activate(String key)

    Admin updatePayment(int plane, Long id, boolean update)
}
