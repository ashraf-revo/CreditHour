package org.revo.service

import org.revo.domain.PT

/**
 * Created by revo on 13/01/16.
 */
interface PTService {
    Set<PT> findAll()

    PT findOne(Long pt)

    PT save(PT pt) throws Exception

}