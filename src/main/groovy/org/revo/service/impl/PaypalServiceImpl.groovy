package org.revo.service.impl

import com.paypal.base.ConfigManager
import com.paypal.base.rest.APIContext
import com.paypal.base.rest.OAuthTokenCredential
import com.paypal.base.rest.PayPalRESTException
import com.paypal.base.rest.PayPalResource
import groovy.util.logging.Log
import org.revo.config.PayPalProperties
import org.revo.service.PaypalService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

import java.util.logging.Level

/**
 * Created by revo on 3/3/16.
 */
@Configuration
@EnableConfigurationProperties(PayPalProperties.class)
@Service
@Log
class PaypalServiceImpl implements PaypalService {
    @Autowired
    private PayPalProperties payPalProperties
    private OAuthTokenCredential OAuthToken
    private APIContext apiContext

    private void initApi(Properties properties) {
        ConfigManager.combineDefaultProperties(properties)
        generateNewOAuthToken(properties)
        PayPalResource.initConfig(properties)
        this.apiContext = new APIContext(OAuthToken.accessToken)
    }

    private void generateNewOAuthToken(Properties properties) {
        String clientID = properties.getProperty("clientId")
        String clientSecret = properties.getProperty("clientSecret")
        Map<String, String> map = new HashMap<>()
        for (final String name : properties.stringPropertyNames()) {
            map.put(name, properties.getProperty(name))
        }
        try {
            OAuthToken = new OAuthTokenCredential(clientID, clientSecret, map)
        } catch (PayPalRESTException e) {
            throw new RuntimeException("error in getting access token", e)
        }
    }

    private Properties generateProperties() {
        Properties properties = new Properties();
        properties.put("clientId", payPalProperties.getClientId());
        properties.put("clientSecret", payPalProperties.getClientSecret());
        properties.put("http.ConnectionTimeOut", "5000");
        properties.put("http.Retry", "1");
        properties.put("http.ReadTimeOut", "30000");
        properties.put("http.MaxConnection", "100");
        properties.put("http.ProxyPort", "8080");
        properties.put("http.ProxyHost", "127.0.0.1");
        properties.put("http.UseProxy", "false");
        properties.put("http.GoogleAppEngine", "false");
        properties.put("service.EndPoint", payPalProperties.getProduction() ? "https://api.paypal.com" : "https://api.sandbox.paypal.com");
        return properties;
    }

    @Override
    public APIContext GetAPIContext() {
        if ((apiContext == null) || (OAuthToken == null) || (OAuthToken.expiresIn() < 0))
            initApi(generateProperties())
        apiContext
    }

    @Scheduled(cron = "0 0 */8 * * ?")
    public void Refresh() {
        initApi(generateProperties())
        log.log(Level.WARNING, "generated at " + new Date())
    }
}
