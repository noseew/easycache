package org.galileo.easycache.core.filter;


import org.galileo.easycache.common.ValWrapper;

public class LoadValContext {

    private String namespace;
    private String cacheName;
    private String fullKey;

    private ValWrapper valWrapper;
    private long start;
    private long opMillis;
    private Throwable throwable;

    public LoadValContext(String namespace, String cacheName, String fullKey) {
        this.namespace = namespace;
        this.cacheName = cacheName;
        this.fullKey = fullKey;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getCacheName() {
        return cacheName;
    }

    public void setCacheName(String cacheName) {
        this.cacheName = cacheName;
    }

    public void setFullKey(String fullKey) {
        this.fullKey = fullKey;
    }

    public Throwable getThrowable() {
        return throwable;
    }

    public void setThrowable(Throwable throwable) {
        this.throwable = throwable;
    }

    public String getFullKey() {
        return fullKey;
    }

    public ValWrapper getValWrapper() {
        return valWrapper;
    }

    public void setValWrapper(ValWrapper valWrapper) {
        this.valWrapper = valWrapper;
    }

    public long getOpMillis() {
        return opMillis;
    }

    public void setOpMillis(long opMillis) {
        this.opMillis = opMillis;
    }

    public long getStart() {
        return start;
    }

    public void setStart(long start) {
        this.start = start;
    }
}
