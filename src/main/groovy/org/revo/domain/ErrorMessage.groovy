package org.revo.domain

import groovy.transform.Canonical

/**
 * Created by revo on 23/01/16.
 */
@Canonical
class ErrorMessage {
    Long Mid
    String Message
    Long Uid
    Long Aid
    Set<String> authorities
    StackTraceElement[] stackTrace

    ErrorMessage(String message) {
        this.Message = message
    }

    ErrorMessage(String message, StackTraceElement[] stackTrace) {
        this.Message = message
        this.stackTrace = stackTrace
    }

    ErrorMessage(Long Mid, String message) {
        this.Mid = Mid
        Message = message
    }

    ErrorMessage(Long Mid, String message, StackTraceElement[] stackTrace) {
        this.Mid = Mid
        this.Message = message
        this.stackTrace = stackTrace
    }

    ErrorMessage users(RevoUser revoUser) {
        this.Uid = revoUser.id
        this.Aid = revoUser.admin
        authorities = revoUser.authorities.collect {
            it.authority
        }
        this
    }
}
