package org.galileo.easycache.springboot.keygenerator;

import org.galileo.easycache.common.KeyGeneratorPolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public abstract class AbsKeyPolicy implements KeyGeneratorPolicy {

    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    public static final LocalVariableTableParameterNameDiscoverer discoverer = new LocalVariableTableParameterNameDiscoverer();


    public static Map<String, Object> objectToMap(Method method, Object... args) {
        Map<String, Object> map = new HashMap<>();
        String[] parameterNames = discoverer.getParameterNames(method);
        if (parameterNames == null || parameterNames.length == 0) {
            return map;
        }
        for (int j = 0; j < parameterNames.length; j++) {
            if (j > args.length) {
                continue;
            }
            map.put(parameterNames[j], args[j]);
        }
        return map;
    }
}
