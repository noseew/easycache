package org.galileo.easycache.common.enums;

public enum CacheExternalType implements IEnumType<CacheExternalType>  {
    REDIS("redis"),
    SPRING_DATA_REDIS("springDataRedis");
    private String value;

    CacheExternalType(String value) {
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
