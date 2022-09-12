package org.galileo.easycache.core.utils;

import org.galileo.easycache.common.SerialPolicy;
import org.galileo.easycache.common.ValWrapper;
import org.galileo.easycache.core.core.EasyCacheManager;

public class InnerCodecUtils {
    private InnerCodecUtils() {

    }

    public static Object deSerialVal(ValWrapper valWrapper) {
        SerialPolicy valueSerialPolicy = EasyCacheManager.getSerialPolicy(valWrapper.getValueSerialName(), null);
        SerialPolicy valueCompressSerialPolicy = EasyCacheManager.getSerialPolicy(valWrapper.getValueCompSerialName(), null);
        if (valueCompressSerialPolicy != null) {
            byte[] uncompress = (byte[]) InnerCodecUtils.decode(valueCompressSerialPolicy, valWrapper.getValue());
            if (valueSerialPolicy != null) {
                return InnerCodecUtils.decode(valueSerialPolicy, uncompress);
            }
        }
        if (valueSerialPolicy != null) {
            return InnerCodecUtils.decode(valueSerialPolicy, valWrapper.getValue());
        }
        return valWrapper.getValue();
    }

    public static Object serialVal(ValWrapper valWrapper, SerialPolicy valueSerial, SerialPolicy valueCompressSerial, int compressThreshold) {
        byte[] val = null;
        if (valWrapper.getValue() != null) {
            val = InnerCodecUtils.encode(valueSerial, valWrapper.getValue());
            if (val != null) {
                if (val.length >= compressThreshold && valueCompressSerial != null) {
                    val = InnerCodecUtils.encode(valueCompressSerial, val);
                    valWrapper.setValueCompSerialName(valueCompressSerial.name());
                }
                valWrapper.setValueSerialName(valueSerial.name());
                valWrapper.setSize(val.length);
            }
        }
        return val;
    }

    public static byte[] encode(SerialPolicy serialPolicy, Object val) {
        if (isByteArray(val)) {
            return (byte[]) val;
        }
        return serialPolicy.encoder().apply(val);
    }

    public static Object decode(SerialPolicy serialPolicy, Object val) {
        if (!isByteArray(val)) {
            return val;
        }
        return serialPolicy.decoder().apply((byte[]) val);
    }

    private static boolean isByteArray(Object val) {
        return val instanceof byte[];
    }

}
