package com.example.exercise;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;

@Configuration
public class AppConfig {

    @Bean
    HashMap<String, String> attempts() {
        return new HashMap<>();
    }
}
