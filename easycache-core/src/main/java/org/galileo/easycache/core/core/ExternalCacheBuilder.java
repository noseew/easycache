package org.galileo.easycache.core.core;


import org.galileo.easycache.common.SerialPolicy;
import org.galileo.easycache.core.core.config.NamespaceConfig;
import org.galileo.easycache.core.filter.BigKeyCheckFilter;
import org.galileo.easycache.core.filter.CircuitBreakerFilter;
import org.galileo.easycache.core.filter.CodecFilter;
import org.galileo.easycache.core.filter.FailCompensateFilter;
import org.galileo.easycache.core.utils.InnerAssertUtils;

public abstract class ExternalCacheBuilder<T extends ExternalCacheBuilder<T>>
        extends AbstractCacheBuilder<ExternalCacheBuilder<T>> {

    protected ExternalCacheBuilder(NamespaceConfig namespaceConfig) {
        super(namespaceConfig);
    }

    public T buildKeySerial(SerialPolicy serialPolicy) {
        InnerAssertUtils.notNull(serialPolicy, "'KeySerial' can not be null");
        InnerAssertUtils.notNull(serialPolicy.decoder(), "'KeySerial' decoder can not be null");
        InnerAssertUtils.notNull(serialPolicy.encoder(), "'KeySerial' encoder can not be null");
        namespaceConfig.setKeySerialName(serialPolicy.name());
        return (T) this;
    }

    public T buildValueSerial(SerialPolicy serialPolicy) {
        InnerAssertUtils.notNull(serialPolicy, "'ValueSerial' can not be null");
        InnerAssertUtils.notNull(serialPolicy.decoder(), "'ValueSerial' decoder can not be null");
        InnerAssertUtils.notNull(serialPolicy.encoder(), "'ValueSerial' encoder can not be null");
        namespaceConfig.setKeySerialName(serialPolicy.name());
        return (T) this;
    }

    public T buildValueWrapperSerial(SerialPolicy serialPolicy) {
        InnerAssertUtils.notNull(serialPolicy, "'ValueWrapperSerial' can not be null");
        InnerAssertUtils.notNull(serialPolicy.decoder(), "'ValueWrapperSerial' decoder can not be null");
        InnerAssertUtils.notNull(serialPolicy.encoder(), "'ValueWrapperSerial' encoder can not be null");
        namespaceConfig.setValueWrapperSerialName(serialPolicy.name());
        return (T) this;
    }

    public T buildValueCompressSerial(SerialPolicy serialPolicy) {
        InnerAssertUtils.notNull(serialPolicy, "'ValueCompressSerial' can not be null");
        InnerAssertUtils.notNull(serialPolicy.decoder(), "'ValueCompressSerial' decoder can not be null");
        InnerAssertUtils.notNull(serialPolicy.encoder(), "'ValueCompressSerial' encoder can not be null");
        namespaceConfig.setValueCompressSerialName(serialPolicy.name());
        return (T) this;
    }

    @Override
    public void preBuild() {
        super.preBuild();
        String namespace = namespaceConfig.getNamespace();

        // 添加 拦截器
        // 编解码器
        addFilters(new CodecFilter(namespaceConfig));
        // 大key检测器
        addFilters(new BigKeyCheckFilter(namespaceConfig.getParent().getBigKey(), namespace));
        // 熔断器
        addFilters(new CircuitBreakerFilter(namespaceConfig.getParent(), namespace));
        // 删除失败补偿器
        addFilters(new FailCompensateFilter(namespace));
    }
}
