package org.galileo.easycache.common;


import java.io.Serializable;

/**
 * 缓存值的包装对象
 */
public class ValWrapper implements Serializable {
    /**
     * 设置的过期时间
     */
    private long expire;
    /**
     * 设置的到期时间戳
     */
    private long expireTs;
    /**
     * 真实的到期时间戳
     */
    private long realExpireTs;
    /**
     * value 序列化策略
     */
    private String valueSerialName;
    /**
     * value 压缩策略
     */
    private String valueCompSerialName;
    /**
     * value 大小, 字节
     */
    private int size = -1;
    /**
     * value
     */
    private Object value;

    public static ValWrapper createInstance(long expire, Object value) {
        ValWrapper wrapper = new ValWrapper();
        wrapper.expire = expire;
        wrapper.expireTs = System.currentTimeMillis() + expire;
        wrapper.value = value;
        wrapper.realExpireTs = wrapper.expireTs;
        return wrapper;
    }

    public long getExpire() {
        return expire;
    }

    public void setExpire(long expire) {
        this.expire = expire;
    }

    public long getExpireTs() {
        return expireTs;
    }

    public void setExpireTs(long expireTs) {
        this.expireTs = expireTs;
    }

    public long getRealExpireTs() {
        return realExpireTs;
    }

    public void setRealExpireTs(long realExpireTs) {
        this.realExpireTs = realExpireTs;
    }

    public String getValueSerialName() {
        return valueSerialName;
    }

    public void setValueSerialName(String valueSerialName) {
        this.valueSerialName = valueSerialName;
    }

    public String getValueCompSerialName() {
        return valueCompSerialName;
    }

    public void setValueCompSerialName(String valueCompSerialName) {
        this.valueCompSerialName = valueCompSerialName;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}
