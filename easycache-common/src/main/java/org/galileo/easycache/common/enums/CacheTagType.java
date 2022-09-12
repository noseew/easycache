package org.galileo.easycache.common.enums;

public enum CacheTagType implements IEnumType<CacheTagType> {
    EASY_CACHE("ec"),
    SET("set"),
    ZSET("zset");
    private String value;

    CacheTagType(String value) {
        this.value = value;
    }

    @Override
    public String getVal() {
        return value;
    }

    @Override
    public IEnumType[] allValues() {
        return values();
    }
}
