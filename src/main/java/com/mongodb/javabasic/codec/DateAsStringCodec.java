package com.mongodb.javabasic.codec;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.codecs.configuration.CodecRegistry;

public class DateAsStringCodec implements Codec<Date>{
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    private Codec<String> stringCodec;
    public DateAsStringCodec(CodecRegistry registry) {
        this.stringCodec = registry.get(String.class);
    }
    @Override
    public void encode(BsonWriter writer, Date value, EncoderContext encoderContext) {
        writer.writeString(sdf.format(value));
    }
    @Override
    public Date decode(BsonReader reader, DecoderContext decoderContext) {
        Date date = new Date();
        try {
            date = sdf.parse(stringCodec.decode(reader, decoderContext));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }
    @Override
    public Class<Date> getEncoderClass() {
        return Date.class;
    }
}
