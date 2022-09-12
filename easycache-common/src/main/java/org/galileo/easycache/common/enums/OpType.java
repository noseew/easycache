package org.galileo.easycache.common.enums;

public enum OpType implements IEnumType<OpType> {
    // @formatter:off
    // 缓存操作 key-val
    GET("cache", "get"),
    PUT("cache", "put"),
    REMOVE("cache", "remove"),
    // Redis操作, set
    ZSET_ADD("zset", "add"),
    ZSET_QUERY("zset", "query"),
    ZSET_REM("zset", "remove"),
    // Redis操作, set
    SET_ADD("set", "add"),
    SET_QUERY("set", "query"),
    SET_REM("set", "remove"),
    NOP("nop", "nop");
    private String value;
    private String subVal;
    // @formatter:on

    OpType(String value, String subVal) {
        this.value = value;
        this.subVal = subVal;
    }

    @Override
    public String getVal() {
        return value;
    }

    public String getSubVal() {
        return subVal;
    }

    @Override
    public IEnumType[] allValues() {
        return values();
    }

    public boolean isCache() {
        return "cache".equals(value);
    }

    public boolean isRedisCollection() {
        return isSet() || isZSet();
    }

    public boolean isZSet() {
        return "zset".equals(value);
    }

    public boolean isSet() {
        return "set".equals(value);
    }

    public boolean isAdd() {
        return this == SET_ADD || this == ZSET_ADD || this == PUT;
    }
}
