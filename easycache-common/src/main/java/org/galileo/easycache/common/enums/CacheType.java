package org.galileo.easycache.common.enums;

public enum CacheType implements IEnumType<CacheType> {
    REMOTE("remote"), LOCAL("local"), BOTH("both");
    private String value;

    CacheType(String value) {
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
