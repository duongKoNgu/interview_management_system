package com.example.demo.config;

import com.example.demo.service.CustomLogoutSuccessHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private CustomLogoutSuccessHandler customLogoutSuccessHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(httpSecurityCsrfConfigurer -> httpSecurityCsrfConfigurer.disable())
                .cors(httpSecurityCorsConfigurer -> httpSecurityCorsConfigurer.configurationSource(request -> new CorsConfiguration().applyPermitDefaultValues()))
                .authorizeRequests(authorizeRequests ->
                        authorizeRequests
                                .requestMatchers("/css/**", "/js/**", "/img/**").permitAll()
                                .requestMatchers("/login", "/forgot-password", "/reset-password","/offer/send-email","/offer/exportExcel","/offer/userLogin" +
                                        "").permitAll()
                                .requestMatchers("/offer/editStatus","/offer/getInterviewNotes","/offer/postSearch","/offer/getdefault","/offer/editStatus","/offer/view","/offer/create","/offer/edit","/offer/getAllOffers").hasAnyRole("ADMIN","MANAGER","RECRUITER")

                                .anyRequest().authenticated()
                )
                .formLogin(formLogin ->
                        formLogin
                                .loginPage("/login")
                                .loginProcessingUrl("/perform_login")
                                .defaultSuccessUrl("/homepage", true)
                                .failureUrl("/login?error=true")
                ).rememberMe(rememberMe ->
                        rememberMe
                                .key("uniqueAndSecret")
                                .tokenValiditySeconds(600)
                )
                .httpBasic(Customizer.withDefaults())
                .logout(logout ->
                        logout
                                .logoutUrl("/logout")
                                .logoutSuccessHandler(customLogoutSuccessHandler)
                                .logoutSuccessUrl("/login?logout=true")
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
