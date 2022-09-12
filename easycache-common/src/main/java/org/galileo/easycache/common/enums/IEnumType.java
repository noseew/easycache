package org.galileo.easycache.common.enums;

public interface IEnumType<T extends IEnumType> {

    String getVal();

    default <T extends IEnumType> T valueOfNull(String val) {
        for (IEnumType iEnumType : allValues()) {
            if (iEnumType.eq(val)) {
                return (T) iEnumType;
            }
        }
        return null;
    }

    IEnumType[] allValues();

    default boolean eq(String o) {
        if (o == null || o.trim().length() == 0) {
            return false;
        }
        return getVal().equalsIgnoreCase(o.trim());
    }
}
