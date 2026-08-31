package com.agrilink.config;

import org.h2.server.web.WebServlet;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class H2ConsoleConfig {
    @Bean
    public ServletRegistrationBean<WebServlet> h2ConsoleServlet() {
        ServletRegistrationBean<WebServlet> registration = new ServletRegistrationBean<>(new WebServlet(), "/h2-console/*");
        registration.addInitParameter("webAllowOthers", "true");
        return registration;
    }
}
