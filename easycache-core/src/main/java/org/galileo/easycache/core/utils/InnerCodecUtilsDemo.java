package org.galileo.easycache.core.utils;

import org.apache.commons.lang3.StringUtils;
import org.galileo.easycache.common.SerialPolicy;
import org.galileo.easycache.common.ValWrapper;
import org.galileo.easycache.core.core.EasyCacheManager;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class InnerCodecUtilsDemo {
    private InnerCodecUtilsDemo() {

    }

    public static ValWrapper deSerialVal(String originalVal) {
        if (StringUtils.isEmpty(originalVal)) {
            return null;
        }
        SerialPolicy jacksonSerialPolicy = EasyCacheManager.getSerialPolicy(SerialPolicy.Jackson, null);
        SerialPolicy gzipSerialPolicy = EasyCacheManager.getSerialPolicy(SerialPolicy.Gzip, null);
        ValWrapper valWrapper = (ValWrapper) jacksonSerialPolicy.decoder()
                .apply(originalVal.getBytes(StandardCharsets.UTF_8));
        if (valWrapper == null || valWrapper.getValue() == null) {
            return valWrapper;
        }
        if (valWrapper.getValue() instanceof String) {
            byte[] valByte = Base64.getDecoder()
                    .decode(((String) valWrapper.getValue()).getBytes(StandardCharsets.UTF_8));
            if (valByte != null) {
                if (valByte.length > 1024 * 512) {
                    valByte = (byte[]) gzipSerialPolicy.decoder().apply(valByte);
                }
                Object val = jacksonSerialPolicy.decoder().apply(valByte);
                valWrapper.setValue(val);
            }
        } else if (valWrapper.getValue() instanceof byte[]) {
            byte[] valByte = (byte[]) valWrapper.getValue();
            if (valByte.length > 1024 * 512) {
                valByte = (byte[]) gzipSerialPolicy.decoder().apply(valByte);
            }
            Object val = jacksonSerialPolicy.decoder().apply(valByte);
            valWrapper.setValue(val);
        }
        return valWrapper;
    }

}
