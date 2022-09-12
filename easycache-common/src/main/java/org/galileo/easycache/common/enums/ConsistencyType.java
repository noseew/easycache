package org.galileo.easycache.common.enums;

public enum ConsistencyType implements IEnumType<ConsistencyType> {
    EVENTUAL("eventual"), STRONG("strong"), DEFAULT("dft");
    private String value;

    ConsistencyType(String value) {
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
