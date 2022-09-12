package org.galileo.easycache.core.filter;


import org.galileo.easycache.common.SerialPolicy;
import org.galileo.easycache.common.ValWrapper;
import org.galileo.easycache.common.enums.CacheType;
import org.galileo.easycache.common.enums.OpType;
import org.galileo.easycache.core.core.EasyCacheManager;
import org.galileo.easycache.core.core.config.NamespaceConfig;
import org.galileo.easycache.core.utils.InnerCodecUtils;

/**
 * 编解码filter, 对远程缓存值进行序列化和反序列化, 如果需要的话
 */
public class CodecFilter extends AbsInvokeFilter {

    private final SerialPolicy valueCompressSerial;
    private final SerialPolicy valueSerial;
    private final NamespaceConfig config;

    public CodecFilter(NamespaceConfig config) {
        super("CodecFilter", config.getNamespace(), null);
        this.config = config;
        valueCompressSerial = EasyCacheManager.getSerialPolicy(config.getValueCompressSerialName(), EasyCacheManager
                .getSerialPolicy(SerialPolicy.Gzip, null));
        valueSerial = EasyCacheManager
                .getSerialPolicy(config.getValueSerialName(), EasyCacheManager.getSerialPolicy(SerialPolicy.Jackson, null));
    }

    @Override
    public Object invoke(FilterContext context) {
        boolean needCodec = CacheType.REMOTE.equals(getTarget().getCacheType());
        ValWrapper valWrapper = context.getValWrapper();
        if (needCodec && OpType.PUT.equals(context.getOpType()) && valWrapper != null) {
            valWrapper.setValue(InnerCodecUtils.serialVal(valWrapper, this.valueSerial, this.valueCompressSerial, config.getCompressThreshold()));
        }
        Object invoke = super.invoke(context);
        if (needCodec && OpType.GET.equals(context.getOpType()) && (valWrapper = context.getValWrapper()) != null) {
            valWrapper.setValue(InnerCodecUtils.deSerialVal(valWrapper));
        }
        return invoke;
    }

    @Override
    protected boolean canProcess(FilterContext context) {
        return false;
    }

}
