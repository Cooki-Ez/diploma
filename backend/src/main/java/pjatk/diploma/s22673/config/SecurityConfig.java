package pjatk.diploma.s22673.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import pjatk.diploma.s22673.services.EmployeeDetailsService;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final EmployeeDetailsService employeeDetailsService;
    private final JWTFilter jwtFilter;

    @Autowired
    public SecurityConfig(EmployeeDetailsService employeeDetailsService, JWTFilter jwtFilter) {
        this.employeeDetailsService = employeeDetailsService;
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder =
                http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder
                .userDetailsService(employeeDetailsService)
                .passwordEncoder(getPasswordEncoder());
        return authenticationManagerBuilder.build();
    }

    @Bean
    public PasswordEncoder getPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/auth/login", "/auth/registration", "/login").permitAll()
                        .requestMatchers("/css/**", "/js/**").permitAll()
                        // Employee endpoints - POST, PATCH, DELETE require MANAGER or ADMIN
                        .requestMatchers(HttpMethod.POST, "/employees/**").hasAnyRole("MANAGER", "ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/employees/**").hasAnyRole("MANAGER", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/employees/**").hasAnyRole("MANAGER", "ADMIN")
                        // Project endpoints - POST, PATCH, DELETE require MANAGER or ADMIN
                        .requestMatchers(HttpMethod.POST, "/projects/**").hasAnyRole("MANAGER", "ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/projects/**").hasAnyRole("MANAGER", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/projects/**").hasAnyRole("MANAGER", "ADMIN")
                        // Department endpoints - POST, PATCH, DELETE require MANAGER or ADMIN
                        .requestMatchers(HttpMethod.POST, "/departments/**").hasAnyRole("MANAGER", "ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/departments/**").hasAnyRole("MANAGER", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/departments/**").hasAnyRole("MANAGER", "ADMIN")
                        // LeaveRequest endpoints - POST allowed for all authenticated users, PATCH, DELETE require MANAGER or ADMIN
                        .requestMatchers(HttpMethod.POST, "/leaves/**").authenticated()
                        .requestMatchers(HttpMethod.PATCH, "/leaves/**").hasAnyRole("MANAGER", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/leaves/**").hasAnyRole("MANAGER", "ADMIN")
                        // All other requests require authentication
                        .anyRequest().authenticated()
                )
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManagerBean(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
        return authenticationManagerBuilder.getOrBuild();
    }
}
