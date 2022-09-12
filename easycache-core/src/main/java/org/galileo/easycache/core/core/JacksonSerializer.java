package org.galileo.easycache.core.core;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectMapper.DefaultTyping;
import com.fasterxml.jackson.databind.deser.std.DateDeserializers;
import com.fasterxml.jackson.databind.ser.std.DateSerializer;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import org.apache.commons.lang3.SerializationException;
import org.galileo.easycache.common.SerialPolicy;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.function.Function;

/**
 * @author songjl
 * @date $
 */
public class JacksonSerializer implements SerialPolicy {

    private final ObjectMapper mapper;
    public static final String SDF_YEAR2DAY = "yyyy-MM-dd";
    public static final String SDF_HOUR2SECOND = "HH:mm:ss.SSS";
    public static final String SDF_YEAR2SECOND = "yyyy-MM-dd HH:mm:ss.SSS";

    @Override
    public String name() {
        return Jackson;
    }

    @Override
    public Function<Object, byte[]> encoder() {
        return e -> {
            try {
                return mapper.writeValueAsBytes(e);
            } catch (JsonProcessingException ex) {
                throw new SerializationException("Could not write JSON: " + ex.getMessage(), ex);
            }
        };
    }

    @Override
    public Function<byte[], Object> decoder() {
        return e -> {
            try {
                return mapper.readValue(e, Object.class);
            } catch (IOException ex) {
                throw new SerializationException("Could not write JSON: " + ex.getMessage(), ex);
            }
        };
    }

    public JacksonSerializer() {
        ObjectMapper mapper = new ObjectMapper();

        final JavaTimeModule javaTimeModule = new JavaTimeModule();
        javaTimeModule.addSerializer(LocalDate.class, new LocalDateSerializer(DateTimeFormatter.ofPattern(SDF_YEAR2DAY)));
        javaTimeModule.addSerializer(LocalTime.class, new LocalTimeSerializer(DateTimeFormatter.ofPattern(SDF_HOUR2SECOND)));
        javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(SDF_YEAR2SECOND)));
        javaTimeModule.addSerializer(Date.class, DateSerializer.instance);

        javaTimeModule.addDeserializer(LocalDate.class, new LocalDateDeserializer(DateTimeFormatter.ofPattern(SDF_YEAR2DAY)));
        javaTimeModule.addDeserializer(LocalTime.class, new LocalTimeDeserializer(DateTimeFormatter.ofPattern(SDF_HOUR2SECOND)));
        javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern(SDF_YEAR2SECOND)));
        javaTimeModule.addDeserializer(Date.class, new DateDeserializers.DateDeserializer());

        mapper.registerModule(javaTimeModule);

        this.mapper = mapper.activateDefaultTyping(mapper.getPolymorphicTypeValidator(), DefaultTyping.NON_FINAL);
    }

}

