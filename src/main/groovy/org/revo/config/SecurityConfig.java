package org.revo.config;

import org.revo.config.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.rememberme.TokenBasedRememberMeServices;
import org.springframework.security.web.csrf.CsrfFilter;

import java.util.Arrays;

/**
 * Created by ashraf on 6/3/15.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    Environment e;
    @Autowired
    UserDetailsService userDetails;
    private String key = "revo_key_remember";

    @Autowired
    private AjaxAuthenticationSuccessHandler Success;

    @Autowired
    private AjaxAuthenticationFailureHandler Failure;

    @Autowired
    private AjaxLogoutSuccessHandler Logout;

    @Autowired
    private Http401UnauthorizedEntryPoint EntryPoint;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .addFilterAfter(new CsrfCookieGeneratorFilter(), CsrfFilter.class)
//                .csrf().disable()
                .exceptionHandling().authenticationEntryPoint(EntryPoint)
                .and().rememberMe().rememberMeServices(rememberMeServices()).key(key)
                .and().formLogin().loginProcessingUrl("/api/authentication").successHandler(Success).failureHandler(Failure).usernameParameter("j_username").passwordParameter("j_password").permitAll()
                .and().logout().logoutUrl("/api/logout").logoutSuccessHandler(Logout).deleteCookies("JSESSIONID", "CSRF-TOKEN").permitAll()
                .and().authorizeRequests()
                .antMatchers("/api/admin/admin/**").hasAnyRole("SETTINGS")
                .antMatchers("/api/admin/**").hasAnyRole("ADMIN")
                .antMatchers("/api/student/**").hasRole("STUDENT")
                .antMatchers("/api/register").permitAll()
                .antMatchers("/api/activate").permitAll()
                .antMatchers("/h2-console/**").permitAll();
        for (String s : e.getActiveProfiles()) {
            if (s.equals("demo")) http.csrf().ignoringAntMatchers("/h2-console/**").and()
                    .headers().frameOptions().disable();

        }
    }

    @Bean
    public RememberMeServices rememberMeServices() {
        TokenBasedRememberMeServices services = new TokenBasedRememberMeServices(key, userDetails);
        services.setAlwaysRemember(true);
        return services;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetails).passwordEncoder(passwordEncoder());
    }
}
