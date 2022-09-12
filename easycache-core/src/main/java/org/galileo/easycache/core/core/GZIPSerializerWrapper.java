package org.galileo.easycache.core.core;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.SerializationException;
import org.galileo.easycache.common.SerialPolicy;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.function.Function;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * 注意, 这里是 byte[] 到 byte[]
 */
public class GZIPSerializerWrapper implements SerialPolicy {

    public static final int BUFFER_SIZE = 4096;

    @Override
    public String name() {
        return Gzip;
    }

    @Override
    public Function<Object, byte[]> encoder() {
        return e -> {
            if (e == null) {
                return new byte[0];
            }
            if (!e.getClass().isArray()) {
                throw new IllegalArgumentException("encoder param must be byte[]");
            }
            if (!(e instanceof byte[])) {
                throw new IllegalArgumentException("encoder param must be byte[]");
            }
            
            ByteArrayOutputStream bos = null;
            GZIPOutputStream gzip = null;
            try {
                bos = new ByteArrayOutputStream();
                gzip = new GZIPOutputStream(bos);
                // 在压缩
                gzip.write((byte[]) e);
                gzip.finish();
                return bos.toByteArray();
            } catch (Exception ex) {
                throw new SerializationException("Gzip Serialization Error", ex);
            } finally {
                IOUtils.closeQuietly(bos);
                IOUtils.closeQuietly(gzip);
            }
        };
    }

    @Override
    public Function<byte[], Object> decoder() {
        return e -> {
            if (e == null || e.length == 0) {
                return null;
            }

            ByteArrayOutputStream bos = null;
            ByteArrayInputStream bis = null;
            GZIPInputStream gzip = null;
            try {
                bos = new ByteArrayOutputStream();
                bis = new ByteArrayInputStream(e);
                gzip = new GZIPInputStream(bis);
                byte[] buff = new byte[BUFFER_SIZE];
                int n;
                // 先解压
                while ((n = gzip.read(buff, 0, BUFFER_SIZE)) > 0) {
                    bos.write(buff, 0, n);
                }
                return bos.toByteArray();
            } catch (Exception ex) {
                throw new SerializationException("Gzip deserizelie error", ex);
            } finally {
                IOUtils.closeQuietly(bos);
                IOUtils.closeQuietly(bis);
                IOUtils.closeQuietly(gzip);
            }
        };
    }
}
