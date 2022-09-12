package org.galileo.easycache.core.core.config;

import org.galileo.easycache.common.constants.CacheConstants;

/**
 * 缓存实例配置
 * 用于创建一个缓存实例, 也就是 Cache 对象
 */
public class NamespaceConfig extends InheritableConfig<EasyCacheConfig> {

    /**
     * 当前namespace名
     */
    private String namespace = CacheConstants.DEFAULT_NAMESPACE;

    /**
     * 缓存组件类型
     */
    private String type;

    /**
     * 本地缓存配置
     */
    private InternalConfig local = new InternalConfig();
    private RemoteConfig remote = new RemoteConfig();

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public InternalConfig getLocal() {
        return local;
    }

    public void setLocal(InternalConfig local) {
        this.local = local;
    }

    public RemoteConfig getRemote() {
        return remote;
    }

    public void setRemote(RemoteConfig remote) {
        this.remote = remote;
    }
}
