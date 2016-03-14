package org.revo.config

import org.h2.server.web.WebServlet
import org.revo.repository.AdminRepository
import org.revo.repository.StudentRepository
import org.revo.service.StudentService
import org.revo.service.util.SomeService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.context.embedded.ServletContextInitializer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.core.env.Environment

import javax.servlet.ServletContext
import javax.servlet.ServletException
import javax.servlet.ServletRegistration

/**
 * Created by ashraf on 2/17/2016.
 */
@Configuration
class SomeConfig implements ServletContextInitializer {
    @Autowired
    SomeService service
    @Autowired
    AdminRepository adminRepository
    @Autowired
    Environment e

    @Profile("init")
    @Bean
    CommandLineRunner initData() {
        { args ->
            if (adminRepository.count() == 0) service.init()
        }
    }

    private void initH2Console(ServletContext servletContext) {
        ServletRegistration.Dynamic h2ConsoleServlet = servletContext.addServlet("H2Console", new WebServlet());
        h2ConsoleServlet.addMapping("/h2-console/*");
        h2ConsoleServlet.setInitParameter("-properties", "src/main/resources");
        h2ConsoleServlet.setLoadOnStartup(1);
    }

    @Override
    void onStartup(ServletContext servletContext) throws ServletException {
        if (e.activeProfiles.contains("h2")) {
            initH2Console(servletContext)
        }
    }
}
