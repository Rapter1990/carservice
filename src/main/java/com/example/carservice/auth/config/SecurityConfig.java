package com.example.carservice.auth.config;

import com.example.carservice.auth.filter.CustomBearerTokenAuthenticationFilter;
import com.example.carservice.auth.security.CustomAuthenticationEntryPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.web.authentication.BearerTokenAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.session.RegisterSessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * Security configuration class for setting up HTTP security, token authentication, and CORS policies.
 * <p>
 * This class configures:
 * <ul>
 *     <li>Custom JWT-based authentication using {@link CustomBearerTokenAuthenticationFilter}</li>
 *     <li>Permitted and secured endpoints</li>
 *     <li>CORS configuration</li>
 *     <li>Session policy (stateless)</li>
 *     <li>Password encoding strategy</li>
 * </ul>
 * </p>
 *
 * @see CustomBearerTokenAuthenticationFilter
 * @see CustomAuthenticationEntryPoint
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfig {

    /**
     * Configures session authentication strategy.
     * <p>
     * This is required when working with session management in Spring Security.
     * In this case, it's used to track authenticated sessions even though the policy is stateless.
     * </p>
     *
     * @return a {@link SessionAuthenticationStrategy} for session registration
     */
    @Bean
    protected SessionAuthenticationStrategy sessionAuthenticationStrategy() {
        return new RegisterSessionAuthenticationStrategy(new SessionRegistryImpl());
    }

    /**
     * Configures the main Spring Security filter chain.
     * <p>
     * Includes configuration for:
     * <ul>
     *     <li>Exception handling with a custom authentication entry point</li>
     *     <li>Permitting public access to authentication, Swagger, and actuator endpoints</li>
     *     <li>Custom JWT authentication filter</li>
     *     <li>CORS and CSRF settings</li>
     * </ul>
     * </p>
     *
     * @param httpSecurity the {@link HttpSecurity} object
     * @param customBearerTokenAuthenticationFilter the custom JWT filter
     * @param customAuthenticationEntryPoint the custom authentication entry point for unauthorized access
     * @return the configured {@link SecurityFilterChain}
     * @throws Exception in case of configuration errors
     */
    @Bean
    public SecurityFilterChain filterChain(
            final HttpSecurity httpSecurity,
            final CustomBearerTokenAuthenticationFilter customBearerTokenAuthenticationFilter,
            final CustomAuthenticationEntryPoint customAuthenticationEntryPoint
    ) throws Exception {

        httpSecurity
                .exceptionHandling(customizer -> customizer.authenticationEntryPoint(customAuthenticationEntryPoint))
                .cors(customizer -> customizer.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(customizer -> customizer
                        .requestMatchers(HttpMethod.POST, "/api/v1/authentication/**").permitAll()
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v2/api-docs/**",
                                "/v3/api-docs/**",
                                "/actuator/**"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .sessionManagement(customizer -> customizer.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(customBearerTokenAuthenticationFilter, BearerTokenAuthenticationFilter.class);

        return httpSecurity.build();
    }

    /**
     * Defines the CORS configuration source.
     * <p>
     * Allows requests from all origins, methods, and headers.
     * </p>
     *
     * @return a {@link CorsConfigurationSource} allowing global cross-origin access
     */
    private CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("*"));
        configuration.setAllowedMethods(List.of("*"));
        configuration.setAllowedHeaders(List.of("*"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    /**
     * Provides the password encoder bean used for hashing user passwords.
     *
     * @return a {@link PasswordEncoder} instance using BCrypt
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
