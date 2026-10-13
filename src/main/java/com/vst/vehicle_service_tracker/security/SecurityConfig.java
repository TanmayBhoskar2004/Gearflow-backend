package com.vst.vehicle_service_tracker.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final CustomUserDetailsService userDetailsService;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthFilter,
                          CustomUserDetailsService userDetailsService) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth

                        // ============ PUBLIC ENDPOINTS ============
                        .requestMatchers("/api/auth/**").permitAll()

                        // ============ ROLE BASE PATHS ============
                        .requestMatchers("/api/admin/**").hasAuthority("ADMIN")
                        .requestMatchers("/api/advisor/**").hasAnyAuthority("SERVICE_ADVISOR", "ADMIN")
                        .requestMatchers("/api/technician/**").hasAnyAuthority("TECHNICIAN", "ADMIN")
                        .requestMatchers("/api/customer/**").hasAnyAuthority("CUSTOMER", "ADMIN")

                        // ============ VEHICLE ENDPOINTS (Day 3) ============
                        .requestMatchers("/api/vehicles/my").hasAnyAuthority("CUSTOMER", "ADMIN")
                        .requestMatchers("/api/vehicles/all").hasAnyAuthority("SERVICE_ADVISOR", "ADMIN")
                        .requestMatchers("/api/vehicles/**")
                        .hasAnyAuthority("CUSTOMER", "SERVICE_ADVISOR", "ADMIN")

                        // ============ SERVICE REQUEST ENDPOINTS (Day 4) ============
                        .requestMatchers("/api/service-requests/my")
                        .hasAnyAuthority("CUSTOMER", "ADMIN")
                        .requestMatchers("/api/service-requests/pending", "/api/service-requests/all")
                        .hasAnyAuthority("SERVICE_ADVISOR", "ADMIN")
                        .requestMatchers("/api/service-requests/**")
                        .hasAnyAuthority("CUSTOMER", "SERVICE_ADVISOR", "ADMIN")

                        // ============ JOB CARD ENDPOINTS (Day 4 + Day 5) ============
                        .requestMatchers("/api/job-cards/my-jobs")
                        .hasAnyAuthority("TECHNICIAN", "ADMIN")
                        .requestMatchers("/api/job-cards/*/status")
                        .hasAnyAuthority("SERVICE_ADVISOR", "TECHNICIAN", "ADMIN")
                        .requestMatchers("/api/job-cards/*/history")
                        .hasAnyAuthority("SERVICE_ADVISOR", "TECHNICIAN", "ADMIN")
                        .requestMatchers("/api/job-cards/**")
                        .hasAnyAuthority("SERVICE_ADVISOR", "ADMIN")

                        // ============ TECHNICIAN ENDPOINTS (Day 5) ============
                        .requestMatchers("/api/technicians/me", "/api/technicians/my-jobs")
                        .hasAnyAuthority("TECHNICIAN", "ADMIN")
                        .requestMatchers("/api/technicians/**")
                        .hasAnyAuthority("SERVICE_ADVISOR", "ADMIN")

                        // ============ PART ENDPOINTS (Day 6) ============
                        .requestMatchers("/api/parts/all", "/api/parts/low-stock")
                        .hasAnyAuthority("INVENTORY_MANAGER", "SERVICE_ADVISOR", "TECHNICIAN", "ADMIN")
                        .requestMatchers("/api/parts/*")
                        .hasAnyAuthority("INVENTORY_MANAGER", "SERVICE_ADVISOR", "TECHNICIAN", "ADMIN")
                        .requestMatchers("/api/parts/**")
                        .hasAnyAuthority("INVENTORY_MANAGER", "ADMIN")

                        // ============ INVENTORY ENDPOINTS (Day 6) ============
                        .requestMatchers("/api/inventory/stock-in")
                        .hasAnyAuthority("INVENTORY_MANAGER", "ADMIN")
                        .requestMatchers("/api/inventory/issue")
                        .hasAnyAuthority("TECHNICIAN", "SERVICE_ADVISOR", "ADMIN")
                        .requestMatchers("/api/inventory/transactions/**")
                        .hasAnyAuthority("INVENTORY_MANAGER", "SERVICE_ADVISOR", "ADMIN")
                        .requestMatchers("/api/inventory/**")
                        .hasAnyAuthority("INVENTORY_MANAGER", "ADMIN")

                        // ============ INVOICE ENDPOINTS (Day 7) ============
                        .requestMatchers("/api/invoices/my")
                        .hasAnyAuthority("CUSTOMER", "ADMIN")
                        .requestMatchers("/api/invoices/generate/*", "/api/invoices/all")
                        .hasAnyAuthority("SERVICE_ADVISOR", "ADMIN")
                        .requestMatchers("/api/invoices/**")
                        .hasAnyAuthority("CUSTOMER", "SERVICE_ADVISOR", "ADMIN")

                        // ============ PAYMENT ENDPOINTS (Day 7) ============
                        .requestMatchers("/api/payments")
                        .hasAnyAuthority("CUSTOMER", "ADMIN")
                        .requestMatchers("/api/payments/**")
                        .hasAnyAuthority("CUSTOMER", "SERVICE_ADVISOR", "ADMIN")

                        // ============ NOTIFICATION ENDPOINTS (Day 8) ============
                        .requestMatchers("/api/notifications/**")
                        .hasAnyAuthority("CUSTOMER", "SERVICE_ADVISOR", "TECHNICIAN",
                                "INVENTORY_MANAGER", "ADMIN")

                        // ============ SERVICE HISTORY ENDPOINTS (Day 8) ============
                        .requestMatchers("/api/service-history/my/**")
                        .hasAnyAuthority("CUSTOMER", "ADMIN")
                        .requestMatchers("/api/service-history/vehicle/**")
                        .hasAnyAuthority("SERVICE_ADVISOR", "ADMIN")

                        // ============ DASHBOARD ENDPOINTS (Day 9) ============
                        .requestMatchers("/api/dashboard/customer")
                        .hasAnyAuthority("CUSTOMER", "ADMIN")
                        .requestMatchers("/api/dashboard/advisor")
                        .hasAnyAuthority("SERVICE_ADVISOR", "ADMIN")
                        .requestMatchers("/api/dashboard/technician")
                        .hasAnyAuthority("TECHNICIAN", "ADMIN")
                        .requestMatchers("/api/dashboard/inventory")
                        .hasAnyAuthority("INVENTORY_MANAGER", "ADMIN")
                        .requestMatchers("/api/dashboard/admin")
                        .hasAuthority("ADMIN")

                        // ============ REPORT ENDPOINTS (Day 9) ============
                        .requestMatchers("/api/reports/**")
                        .hasAnyAuthority("SERVICE_ADVISOR", "INVENTORY_MANAGER", "ADMIN")

                        // ============ DEFAULT ============
                        .anyRequest().authenticated()
                )
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}