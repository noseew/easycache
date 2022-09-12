package org.galileo.easycache.core.core;



import org.galileo.easycache.common.SerialPolicy;
import org.galileo.easycache.core.utils.InnerAssertUtils;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.function.Function;

public class StringSerializerWrapper implements SerialPolicy {

    private Charset charset = StandardCharsets.UTF_8;

    public StringSerializerWrapper(Charset charset) {
        InnerAssertUtils.notNull(charset, "charset can not be null");
        this.charset = charset;
    }

    @Override
    public String name() {
        return STRING;
    }

    @Override
    public Function<Object, byte[]> encoder() {
        return e -> (e == null ? null : e.toString().getBytes(charset));
    }

    @Override
    public Function<byte[], Object> decoder() {
        return e -> (e == null ? null : new String(e, charset));
    }
}
