package com.agrilink.config;

import com.agrilink.security.JwtFilter;
import org.springframework.context.annotation.*;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.*;
import java.util.List;

@Configuration
public class SecurityConfig {
    @Bean PasswordEncoder passwordEncoder(){return new BCryptPasswordEncoder();}
    
    @Bean CorsConfigurationSource corsConfigurationSource(){
        var c=new CorsConfiguration(); 
        c.setAllowedOriginPatterns(List.of("*")); 
        c.setAllowedMethods(List.of("GET","POST","PUT","DELETE","OPTIONS")); 
        c.setAllowedHeaders(List.of("*"));
        c.setAllowCredentials(true);
        var s=new UrlBasedCorsConfigurationSource(); 
        s.registerCorsConfiguration("/**",c); 
        return s;
    }
    
    @Bean SecurityFilterChain security(HttpSecurity h, JwtFilter jwt) throws Exception {
        return h
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .httpBasic(basic -> basic.disable())
            .formLogin(form -> form.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(AntPathRequestMatcher.antMatcher("/")).permitAll()
                .requestMatchers(AntPathRequestMatcher.antMatcher("/*.html")).permitAll()
                .requestMatchers(AntPathRequestMatcher.antMatcher("/*.css")).permitAll()
                .requestMatchers(AntPathRequestMatcher.antMatcher("/*.js")).permitAll()
                .requestMatchers(AntPathRequestMatcher.antMatcher("/*.ico")).permitAll()
                .requestMatchers(AntPathRequestMatcher.antMatcher("/robots.txt")).permitAll()
                .requestMatchers(AntPathRequestMatcher.antMatcher("/sitemap.xml")).permitAll()
                .requestMatchers(AntPathRequestMatcher.antMatcher("/error")).permitAll()
                .requestMatchers(AntPathRequestMatcher.antMatcher("/api/auth/**")).permitAll()
                .requestMatchers(AntPathRequestMatcher.antMatcher("/api/public/**")).permitAll()
                .requestMatchers(AntPathRequestMatcher.antMatcher("/h2-console/**")).permitAll()
                .anyRequest().authenticated()
            )
            .headers(headers -> headers.frameOptions(f -> f.sameOrigin()))
            .addFilterBefore(jwt, UsernamePasswordAuthenticationFilter.class)
            .build();
    }
}

