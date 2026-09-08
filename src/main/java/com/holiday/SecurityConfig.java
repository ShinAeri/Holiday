package com.holiday;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtTokenProvider tokenProvider;
    private final Environment environment;

    public SecurityConfig(JwtTokenProvider tokenProvider, Environment environment) {
        this.tokenProvider = tokenProvider;
        this.environment = environment;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        // 현재 release 프로파일로 실행 중인지 확인
        boolean isRelease = Arrays.asList(environment.getActiveProfiles()).contains("release");

        http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> {
                // 로그인, 회원가입 API 허용
                auth.requestMatchers("/api/auth/**").permitAll();

                // 에러 디스패치 경로 허용 (없으면 404가 403으로 뒤바뀜)
                auth.requestMatchers("/error").permitAll();

                // release 환경이 아닐 때만(즉, 평상시 개발할 때) Swagger 경로 허용
                if (!isRelease) {
                    auth.requestMatchers(
                            "/swagger-ui/**",
                            "/swagger-ui.html",
                            "/v3/api-docs/**",
                            "/swagger-resources/**",
                            "/webjars/**"
                    ).permitAll();
                }

                auth.anyRequest().authenticated();
            })
            .addFilterBefore(new JwtAuthenticationFilter(tokenProvider), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}