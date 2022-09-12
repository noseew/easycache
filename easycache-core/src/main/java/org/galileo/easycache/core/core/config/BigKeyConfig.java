package org.galileo.easycache.core.core.config;


import org.galileo.easycache.common.constants.CacheConstants;

public class BigKeyConfig {

    /**
     * 开关
     */
    private boolean enableSizeLimit = CacheConstants.BIGKEY_ENABLESIZELIMIT;
    /**
     * 预警的 缓存大小
     */
    private String warnSize = String.valueOf(CacheConstants.BIGKEY_WARNSIZE);
    /**
     * 预警的 集合类型缓存长度
     */
    private int warnLen = CacheConstants.BIGKEY_WARNLEN;
    /**
     * 拒绝的 缓存大小
     */
    private String forbiddenSize = String.valueOf(CacheConstants.BIGKEY_FORBIDDENSIZE);
    /**
     * 拒绝的 集合类型缓存长度
     */
    private int forbiddenLen = CacheConstants.BIGKEY_FORBIDDENLEN;
    /**
     * 拒绝后是否异常
     */
    private boolean forbiddenException = CacheConstants.FORBIDDEN_EXCEPTION;

    public boolean isForbiddenException() {
        return forbiddenException;
    }

    public void setForbiddenException(boolean forbiddenException) {
        this.forbiddenException = forbiddenException;
    }

    public boolean isEnableSizeLimit() {
        return enableSizeLimit;
    }

    public void setEnableSizeLimit(boolean enableSizeLimit) {
        this.enableSizeLimit = enableSizeLimit;
    }

    public String getWarnSize() {
        return warnSize;
    }

    public void setWarnSize(String warnSize) {
        this.warnSize = warnSize;
    }

    public String getForbiddenSize() {
        return forbiddenSize;
    }

    public void setForbiddenSize(String forbiddenSize) {
        this.forbiddenSize = forbiddenSize;
    }

    public int getWarnLen() {
        return warnLen;
    }

    public void setWarnLen(int warnLen) {
        this.warnLen = warnLen;
    }

    public int getForbiddenLen() {
        return forbiddenLen;
    }

    public void setForbiddenLen(int forbiddenLen) {
        this.forbiddenLen = forbiddenLen;
    }
}
