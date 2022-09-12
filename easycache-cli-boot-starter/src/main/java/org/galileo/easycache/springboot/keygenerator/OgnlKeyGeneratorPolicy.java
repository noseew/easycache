package org.galileo.easycache.springboot.keygenerator;

import ognl.DefaultClassResolver;
import ognl.DefaultTypeConverter;
import ognl.MemberAccess;
import ognl.Ognl;
import ognl.OgnlContext;
import ognl.OgnlException;
import org.apache.commons.lang3.StringUtils;
import org.galileo.easycache.anno.CacheRemove;
import org.galileo.easycache.anno.CacheRemoveAll;
import org.galileo.easycache.anno.CacheUpdate;
import org.galileo.easycache.anno.Cached;
import org.galileo.easycache.springboot.aop.AnnoAttributeUtil;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 未测试
 */
@Deprecated
public class OgnlKeyGeneratorPolicy extends AbsKeyPolicy {

    @Override
    public Object generateKey(Object target, Method method, Object... args) {
        Cached cached = method.getAnnotation(Cached.class);
        CacheRemove cacheRemove = method.getAnnotation(CacheRemove.class);
        CacheRemoveAll cacheRemoveAll = method.getAnnotation(CacheRemoveAll.class);
        CacheUpdate cacheUpdate = method.getAnnotation(CacheUpdate.class);
        String key = AnnoAttributeUtil.getKey(cached, cacheRemove, cacheUpdate);
        if (StringUtils.isEmpty(key)) {
            return "";
        }

        Map<String, Object> map = objectToMap(method, args);// 构建一个OgnlContext对象
        OgnlContext context = (OgnlContext) Ognl.createDefaultContext(map,
                new DefaultMemberAccess(true),
                new DefaultClassResolver(),
                new DefaultTypeConverter());
        context.setRoot(map);

        if (cacheRemoveAll != null) {
            return Arrays.stream(cacheRemoveAll.value()).map(e -> {
                if (StringUtils.isEmpty(e.key())) {
                    return "";
                }
                try {
                    return Ognl.getValue(e.key(), context, context.getRoot());
                } catch (OgnlException ognlException) {
                    logger.warn("EasyCache OgnlKeyGeneratorPolicy cacheRemoveAll getValue error, key={}, e", e.key(), ognlException);
                    return e.key();
                }
            }).collect(Collectors.toSet());
        }
        try {
            return Ognl.getValue(key, context, context.getRoot());
        } catch (OgnlException e) {
            logger.warn("EasyCache OgnlKeyGeneratorPolicy getValue error, key={}, e", key, e);
            return key;
        }
    }

    public static class DefaultMemberAccess implements MemberAccess {
        private boolean allowPrivateAccess = false;
        private boolean allowProtectedAccess = false;
        private boolean allowPackageProtectedAccess = false;

        public DefaultMemberAccess(boolean allowAllAccess) {
            this(allowAllAccess, allowAllAccess, allowAllAccess);
        }

        public DefaultMemberAccess(boolean allowPrivateAccess, boolean allowProtectedAccess,
                boolean allowPackageProtectedAccess) {
            super();
            this.allowPrivateAccess = allowPrivateAccess;
            this.allowProtectedAccess = allowProtectedAccess;
            this.allowPackageProtectedAccess = allowPackageProtectedAccess;
        }

        @Override
        public Object setup(Map context, Object target, Member member, String propertyName) {
            Object result = null;

            if (isAccessible(context, target, member, propertyName)) {
                AccessibleObject accessible = (AccessibleObject) member;

                if (!accessible.isAccessible()) {
                    result = Boolean.TRUE;
                    accessible.setAccessible(true);
                }
            }
            return result;
        }

        @Override
        public void restore(Map context, Object target, Member member, String propertyName, Object state) {
            if (state != null) {
                ((AccessibleObject) member).setAccessible((Boolean) state);
            }
        }

        /**
         * Returns true if the given member is accessible or can be made accessible by this object.
         */
        @Override
        public boolean isAccessible(Map context, Object target, Member member, String propertyName) {
            int modifiers = member.getModifiers();
            if (Modifier.isPublic(modifiers)) {
                return true;
            } else if (Modifier.isPrivate(modifiers)) {
                return this.allowPrivateAccess;
            } else if (Modifier.isProtected(modifiers)) {
                return this.allowProtectedAccess;
            } else {
                return this.allowPackageProtectedAccess;
            }
        }

        public boolean isAllowPrivateAccess() {
            return allowPrivateAccess;
        }

        public void setAllowPrivateAccess(boolean allowPrivateAccess) {
            this.allowPrivateAccess = allowPrivateAccess;
        }

        public boolean isAllowProtectedAccess() {
            return allowProtectedAccess;
        }

        public void setAllowProtectedAccess(boolean allowProtectedAccess) {
            this.allowProtectedAccess = allowProtectedAccess;
        }

        public boolean isAllowPackageProtectedAccess() {
            return allowPackageProtectedAccess;
        }

        public void setAllowPackageProtectedAccess(boolean allowPackageProtectedAccess) {
            this.allowPackageProtectedAccess = allowPackageProtectedAccess;
        }
    }
}
