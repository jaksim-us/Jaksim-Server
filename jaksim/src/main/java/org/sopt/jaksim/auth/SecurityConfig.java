package org.sopt.jaksim.auth;

import lombok.RequiredArgsConstructor;
import org.sopt.jaksim.auth.filter.CustomAccessDeniedHandler;
import org.sopt.jaksim.auth.filter.CustomJwtAuthenticationEntryPoint;
import org.sopt.jaksim.auth.filter.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.RequestCacheConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import java.util.Arrays;
import java.util.Collections;

import static org.sopt.jaksim.global.common.Constants.ACTIVATE_PROFILE_URL;
import static org.sopt.jaksim.global.common.Constants.WEB_SOCKET_SERVER_URL;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity //web Security를 사용할 수 있게
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CustomJwtAuthenticationEntryPoint customJwtAuthenticationEntryPoint;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;

    private static final String[] AUTH_WHITE_LIST = {ACTIVATE_PROFILE_URL,
            WEB_SOCKET_SERVER_URL, "/login/**", "/api/v1/auth/**",
            "/swagger-ui/**", "/swagger-resources/**"};

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.cors((cors) -> cors
                .configurationSource(request -> {
                    CorsConfiguration configuration = new CorsConfiguration();
                    configuration.setAllowedOriginPatterns(Arrays.asList("*"));
                    configuration.setAllowedMethods(Collections.singletonList("*"));
                    configuration.setAllowCredentials(true);
                    configuration.setAllowedHeaders(Collections.singletonList("*"));
                    configuration.setMaxAge(3600L);
                    configuration.setExposedHeaders(Collections.singletonList("Authorization"));
                    return configuration;
                }));
        http.csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .requestCache(RequestCacheConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .exceptionHandling(exception ->
                {
                    exception.authenticationEntryPoint(customJwtAuthenticationEntryPoint);
                    exception.accessDeniedHandler(customAccessDeniedHandler);
                });


        http.authorizeHttpRequests((auth) -> auth
            .requestMatchers(AUTH_WHITE_LIST).permitAll()
            .anyRequest().authenticated()
        )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }


}
