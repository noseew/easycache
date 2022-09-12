package org.galileo.easycache.springboot.springdata;

import org.galileo.easycache.common.SerialPolicy;
import org.springframework.data.redis.serializer.RedisSerializer;

import java.util.function.Function;

public class JacksonRedisSerial implements SerialPolicy {

    static RedisSerializer<Object> jacksonRedisSerializer = RedisSerializer.json();

    @Override
    public String name() {
        return Jackson;
    }

    @Override
    public Function<Object, byte[]> encoder() {
        return e -> jacksonRedisSerializer.serialize(e);
    }

    @Override
    public Function<byte[], Object> decoder() {
        return e -> jacksonRedisSerializer.deserialize(e);
    }
}
