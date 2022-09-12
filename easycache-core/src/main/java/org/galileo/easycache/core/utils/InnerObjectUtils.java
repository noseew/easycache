package org.galileo.easycache.core.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class InnerObjectUtils {

    private static Logger logger = LoggerFactory.getLogger(InnerObjectUtils.class);

    private InnerObjectUtils() {

    }

    public static boolean eq(Object o1, Object o2) {
        if (o1 == null && o2 == null) {
            return true;
        }
        return Objects.equals(o1, o2);
    }

    public static Map<String, Object> objectToMap(Object obj) {
        if (obj == null) {
            return null;
        }

        Map<String, Object> map = new HashMap<>();
        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(obj.getClass());
            PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
            for (PropertyDescriptor property : propertyDescriptors) {
                String key = property.getName();
                if (key.compareToIgnoreCase("class") == 0) {
                    continue;
                }
                Method getter = property.getReadMethod();
                Object value = getter != null ? getter.invoke(obj) : null;
                map.put(key, value);
            }
        } catch (Exception e) {
            // ignore
            logger.warn("", e);
        }

        return map;
    }
}
