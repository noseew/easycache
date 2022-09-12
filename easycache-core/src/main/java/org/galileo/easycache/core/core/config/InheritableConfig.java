package org.galileo.easycache.core.core.config;

import org.apache.commons.lang3.StringUtils;
import org.galileo.easycache.common.constants.CacheConstants;

import java.time.Duration;

public class InheritableConfig<P extends InheritableConfig> {
    /**
     *
     */
    private P parent;
    /**
     *
     */
    private Duration expire;
    /**
     *
     */
    private String consistency;

    /**
     *
     */
    private String keySerialName;
    /**
     *
     */
    private String valueWrapperSerialName;
    /**
     *
     */
    private String valueCompressSerialName;
    /**
     *
     */
    private String valueSerialName;
    /**
     *
     */
    private int compressThreshold = CacheConstants.COMPRESS_THRESHOLD;

    public P getParent() {
        return parent;
    }

    public void setParent(P parent) {
        this.parent = parent;
    }

    public Duration getExpire() {
        if (expire.toMillis() <= 0 && parent != null) {
            return parent.getExpire();
        }
        return expire;
    }

    public void setExpire(Duration expire) {
        this.expire = expire;
    }

    public String getConsistency() {
        if (StringUtils.isEmpty(consistency) && parent != null) {
            return parent.getConsistency();
        }
        return consistency;
    }

    public void setConsistency(String consistency) {
        this.consistency = consistency;
    }

    public String getKeySerialName() {
        if (StringUtils.isEmpty(keySerialName) && parent != null) {
            return parent.getKeySerialName();
        }
        return keySerialName;
    }

    public void setKeySerialName(String keySerialName) {
        this.keySerialName = keySerialName;
    }

    public String getValueWrapperSerialName() {
        if (StringUtils.isEmpty(valueWrapperSerialName) && parent != null) {
            return parent.getValueWrapperSerialName();
        }
        return valueWrapperSerialName;
    }

    public void setValueWrapperSerialName(String valueWrapperSerialName) {
        this.valueWrapperSerialName = valueWrapperSerialName;
    }

    public String getValueCompressSerialName() {
        if (StringUtils.isEmpty(valueCompressSerialName) && parent != null) {
            return parent.getValueCompressSerialName();
        }
        return valueCompressSerialName;
    }

    public void setValueCompressSerialName(String valueCompressSerialName) {
        this.valueCompressSerialName = valueCompressSerialName;
    }

    public String getValueSerialName() {
        if (StringUtils.isEmpty(valueSerialName) && parent != null) {
            return parent.getValueSerialName();
        }
        return valueSerialName;
    }

    public void setValueSerialName(String valueSerialName) {
        this.valueSerialName = valueSerialName;
    }

    public int getCompressThreshold() {
        if (compressThreshold <= 0 && parent != null) {
            return parent.getCompressThreshold();
        }
        return compressThreshold;
    }

    public void setCompressThreshold(int compressThreshold) {
        this.compressThreshold = compressThreshold;
    }
}
