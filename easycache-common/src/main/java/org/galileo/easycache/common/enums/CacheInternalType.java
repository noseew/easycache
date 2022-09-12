package org.galileo.easycache.common.enums;

public enum CacheInternalType  implements IEnumType<CacheInternalType>  {
    CAFFEINE("caffeine");
    private String value;

    CacheInternalType(String value) {
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
