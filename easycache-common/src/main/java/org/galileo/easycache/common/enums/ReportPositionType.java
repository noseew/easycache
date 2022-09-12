package org.galileo.easycache.common.enums;

public enum ReportPositionType implements IEnumType<ReportPositionType> {
    LOG("log"), REMOTE("remote"), BOTH("both");
    private String value;

    ReportPositionType(String value) {
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
