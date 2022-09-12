package org.galileo.easycache.common;


import org.galileo.easycache.common.enums.CacheType;

/**
 * 用于使用该接口创建Cache代理类的，和Cache接口相比，只是做功能上的区分
 */
public interface CacheProxy extends CacheClient {

    default Object serialVal(ValWrapper valWrapper) {
        return valWrapper.getValue();
    }

    default Object deSerialVal(ValWrapper valWrapper) {
        return valWrapper.getValue();
    }

    /**
     * 获取当前代理类的目标类, 也就是当前类自己
     * 
     * @return
     */
    default CacheProxy unProxy() {
        return this;
    }

    /**
     * 获取当前类的代理类
     * 
     * @return
     */
    default CacheProxy getProxy() {
        return null;
    }

    default void setProxy(CacheProxy proxy) {
        
    }

    default void setFilter(Filter filter) {

    }
    default Filter getFilter() {
        return null;
    }
    
    default CacheLock getCacheLocker(boolean biasBlock) {
        return null;
    }

    CacheType getCacheType();


}
