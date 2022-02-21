package com.yiigaa.once;

import com.yiigaa.once.controllercommon.ControllerFilter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.EnableTransactionManagement;


import java.util.ArrayList;
import java.util.List;

@EnableTransactionManagement
@SpringBootApplication
public class onceApplication extends SpringBootServletInitializer{

    public static void main(String[] args) {
        SpringApplication.run(onceApplication.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(onceApplication.class);
    }

    //If you want to forbidden session, open annotation
    // @Bean
    // public FilterRegistrationBean filterRegistrationBean() {
    //     FilterRegistrationBean registrationBean = new FilterRegistrationBean();
    //     registrationBean.setFilter(new ControllerFilter());
    //     return registrationBean;
    // }

}
