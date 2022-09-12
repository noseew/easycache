package org.galileo.easycache.common.enums;

public enum BreakdownType implements IEnumType<BreakdownType> {
    NONE("none"),
    RENEWAL("renewal"),
    DEFAULT("dft")
    ;
    private String value;

    BreakdownType(String value) {
        this.value = value;
    }

    @Override
    public IEnumType[] allValues() {
        return values();
    }

    @Override
    public String getVal() {
        return value;
    }
}
