package com.mongodb.javabasic.converter;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.stereotype.Component;
@Component
@WritingConverter
public class DateToStringConverter implements Converter<Date, String> {
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    public String convert(Date date) {
        return sdf.format(date);
    }
}
