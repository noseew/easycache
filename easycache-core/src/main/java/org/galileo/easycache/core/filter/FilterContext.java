package org.galileo.easycache.core.filter;

import com.google.common.base.Objects;
import org.galileo.easycache.common.CacheProxy;
import org.galileo.easycache.common.ValWrapper;
import org.galileo.easycache.common.constants.CacheConstants;
import org.galileo.easycache.common.enums.OpType;
import org.galileo.easycache.core.core.config.NamespaceConfig;
import org.galileo.easycache.core.core.config.RemoteConfig;
import org.galileo.easycache.core.utils.InnerObjectUtils;

import java.lang.reflect.Method;
import java.util.Set;

/**
 * 缓存filter的上下文, 每次缓存操作如果需要经过filter, 则需要先生成该上下文, 表示本次缓存操作各种信息
 */
public class FilterContext {
    /**
     * 缓存key
     */
    private final String key;
    /**
     * 缓存key
     */
    private final Set<String> keys;
    /**
     * 缓存操作类型
     */
    private final OpType opType;

    /**
     * 缓存操作的实例对象
     */
    private CacheProxy cache;
    /**
     * 缓存实例对象所属的配置
     */
    private RemoteConfig remoteConfig;
    /**
     * 目标方法
     */
    private Method method;
    /**
     * 目标方法参数
     */
    private Object[] args;
    /**
     * 缓存值结果或者入参(具体要看缓存操作类型)
     */
    private ValWrapper valWrapper;
    /**
     * 非key-val类型的缓存的此次操作val
     */
    private Object val;
    /**
     * 缓存操作结果code
     */
    private int resCode = CacheConstants.CODE_OK;
    /**
     * 缓存操作时间, 单位毫秒
     */
    private long opMillis;
    /**
     * 缓存是否命中
     */
    private boolean hit;

    public FilterContext(CacheProxy cache, RemoteConfig remoteConfig, String key, Set<String> keys,
                         OpType opType, Method method, Object[] args) {
        this.cache = cache;
        this.key = key;
        this.keys = keys;
        this.remoteConfig = remoteConfig;
        this.opType = opType;
        this.method = method;
        this.args = args;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof FilterContext)) {
            return false;
        }
        FilterContext that = (FilterContext) o;
        return InnerObjectUtils.eq(key, that.key) && InnerObjectUtils.eq(keys, that.keys) && opType == that.opType;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(key, keys, opType);
    }

    public String getKey() {
        return key;
    }

    public Set<String> getKeys() {
        return keys;
    }

    public OpType getOpType() {
        return opType;
    }

    public CacheProxy getCache() {
        return cache;
    }

    public void setCache(CacheProxy cache) {
        this.cache = cache;
    }

    public RemoteConfig getRemoteConfig() {
        return remoteConfig;
    }

    public void setRemoteConfig(RemoteConfig remoteConfig) {
        this.remoteConfig = remoteConfig;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public Object[] getArgs() {
        return args;
    }

    public void setArgs(Object[] args) {
        this.args = args;
    }

    public ValWrapper getValWrapper() {
        return valWrapper;
    }

    public void setValWrapper(ValWrapper valWrapper) {
        this.valWrapper = valWrapper;
    }

    public int getResCode() {
        return resCode;
    }

    public void setResCode(int resCode) {
        this.resCode = resCode;
    }

    public boolean isHit() {
        return hit;
    }

    public void setHit(boolean hit) {
        this.hit = hit;
    }

    public long getOpMillis() {
        return opMillis;
    }

    public void setOpMillis(long opMillis) {
        this.opMillis = opMillis;
    }

    public Object getVal() {
        return val;
    }

    public void setVal(Object val) {
        this.val = val;
    }
}
