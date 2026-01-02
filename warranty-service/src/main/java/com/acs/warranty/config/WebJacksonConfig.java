package com.acs.warranty.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.context.annotation.Bean;

@Configuration
public class WebJacksonConfig {

    /**
     * Explicit Jackson converter for REST APIs
     * Fixes LocalDate / LocalDateTime serialization permanently
     */
    @Bean
    public MappingJackson2HttpMessageConverter jackson2Converter() {

        ObjectMapper mapper = new ObjectMapper();

        // ✅ Java 8 Date/Time support
        mapper.registerModule(new JavaTimeModule());

        // ✅ ISO-8601 format (yyyy-MM-dd)
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        return new MappingJackson2HttpMessageConverter(mapper);
    }
}
