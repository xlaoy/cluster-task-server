package com.task.server.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.client.RestTemplate;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Created by Administrator on 2018/8/19 0019.
 */
@Configuration
public class BeanConfig {

    public static final String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";

    public static final String YYYY_MM_DD = "yyyy-MM-dd";

    public static final String HH_MM_SS = "HH:mm:ss";

    public static final String TIMEZONE = "GMT+8";

    public static final String LOCALE = "zh";

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(YYYY_MM_DD_HH_MM_SS)));
        javaTimeModule.addSerializer(LocalDate.class, new LocalDateSerializer(DateTimeFormatter.ofPattern(YYYY_MM_DD)));
        javaTimeModule.addSerializer(LocalTime.class, new LocalTimeSerializer(DateTimeFormatter.ofPattern(HH_MM_SS)));
        mapper.registerModule(javaTimeModule);
        mapper.setDateFormat(new SimpleDateFormat(YYYY_MM_DD_HH_MM_SS));
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.configure(SerializationFeature.INDENT_OUTPUT, Boolean.TRUE);
        return mapper;
    }

    @Bean
    public ThreadPoolTaskExecutor sendRequestkExecutor() {
        ThreadPoolTaskExecutor sendRequestkExecutor = new ThreadPoolTaskExecutor();
        sendRequestkExecutor.setCorePoolSize(5);
        sendRequestkExecutor.setMaxPoolSize(10);
        sendRequestkExecutor.setKeepAliveSeconds(3000);
        return sendRequestkExecutor;
    }

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        builder.setReadTimeout(5000);
        builder.setConnectTimeout(3000);
        return builder.build();
    }
}
