package org.revo.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "paypal")
class PayPalProperties {
    String clientId
    String clientSecret
    String successUrl
    String failUrl
    Boolean production

}
