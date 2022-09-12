package org.galileo.easycache.springboot.keygenerator;

import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Component
public class NoneKeyGeneratorPolicy extends AbsKeyPolicy {

    @Override
    public Object generateKey(Object target, Method method, Object... args) {
        return "";
    }
}
