package com.mongodb.javabasic;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;

import com.mongodb.javabasic.converter.LocalDateTimeToStringConverter;
import com.mongodb.javabasic.converter.StringToLocalDateTimeConverter;


@Configuration
public class DBConfig {
    @Bean
    public MongoCustomConversions mongoCustomConversions() {
  
      return new MongoCustomConversions(
          Arrays.asList(
              new StringToLocalDateTimeConverter(),
              new LocalDateTimeToStringConverter()));
    }
}
