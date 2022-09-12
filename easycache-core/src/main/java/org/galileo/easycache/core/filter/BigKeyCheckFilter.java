package org.galileo.easycache.core.filter;


import org.galileo.easycache.common.NativeRedisClient;
import org.galileo.easycache.common.ValWrapper;
import org.galileo.easycache.common.constants.CacheConstants;
import org.galileo.easycache.common.enums.CacheType;
import org.galileo.easycache.common.enums.OpType;
import org.galileo.easycache.core.core.config.BigKeyConfig;
import org.galileo.easycache.core.exception.CacheInterruptException;
import org.galileo.easycache.core.utils.InnerSizeUtils;

/**
 * 用于检测和管理 bigkey 的 filter
 * 可通过配置对bigkey进行预警和终止缓存操作
 * 
 * 注意: 该filter仅支持远程缓存, 例如redis; 同时该filter需要在缓存值序列化之后才能得到缓存大小, 所以需要在 CodecFilter 之后, 
 * 否则该filter 将不生效
 */
public class BigKeyCheckFilter extends AbsInvokeFilter {

    private final BigKeyConfig bigKeyConfig;

    public BigKeyCheckFilter(BigKeyConfig bigKeyConfig, String namespace) {
        super("BigKeyCheck", namespace, null);
        this.bigKeyConfig = bigKeyConfig;
    }

    @Override
    public Object invoke(FilterContext context) {
        boolean canProcess = canProcess(context);
        if (!canProcess) {
            return super.invoke(context);
        }

        if (OpType.PUT.equals(context.getOpType()) && !cachePass(context)) {
            // 忽略缓存
            context.setResCode(CacheConstants.CODE_ERROR_FILTER);
            return null;
        }
        if (OpType.ZSET_ADD.equals(context.getOpType()) || OpType.SET_ADD.equals(context.getOpType())) {
            // 忽略缓存
            if (!collectionPass(context)) {
                context.setResCode(CacheConstants.CODE_ERROR_FILTER);
                return null;
            }
        }
        return super.invoke(context);
    }

    @Override
    protected boolean canProcess(FilterContext context) {
        return bigKeyConfig.isEnableSizeLimit() && CacheType.REMOTE.equals(context.getCache().getCacheType());
    }

    /**
     * key-val 类型bigkey校验
     *
     * @param context
     * @return
     */
    public boolean cachePass(FilterContext context) {
        ValWrapper valWrapper = context.getValWrapper();
        if (valWrapper == null || valWrapper.getValue() == null) {
            return true;
        }
        return sizePass(context.getKey(), valWrapper.getSize());
    }

    /**
     * set/zset 类型bigkey校验
     *
     * @param context
     * @return
     */
    public boolean collectionPass(FilterContext context) {
        String key = context.getKey();
        NativeRedisClient nativeRedisClient = context.getCache().getNativeRedisClient();
        long len = 0;
        if (OpType.ZSET_ADD.equals(context.getOpType())) {
            len = nativeRedisClient.zcount(key, Double.MIN_VALUE, Double.MAX_VALUE);
        } else if (OpType.SET_ADD.equals(context.getOpType())) {
            len = nativeRedisClient.scard(key);
        }
        return lenPass(key, len) && collectionValPass(key, context.getVal());
    }

    private boolean collectionValPass(String key, Object val) {
        if (val == null) {
            return true;
        }
        if (val instanceof String[]) {
            for (String v : (String[]) val) {
                if (v != null && !sizePass(key, v.length())) {
                    return false;
                }
            }
        }
        if (val instanceof String) {
            return sizePass(key, ((String) val).length());
        }
        return true;
    }

    /**
     * 字节大小校验
     *
     * @param key
     * @param valSize
     * @return
     */
    private boolean sizePass(String key, int valSize) {
        String realSizeString = InnerSizeUtils.sizeFormat(valSize);
        String warnSizeString = InnerSizeUtils.sizeFormat(InnerSizeUtils.parseSize(bigKeyConfig.getWarnSize()));
        if (valSize >= InnerSizeUtils.parseSize(bigKeyConfig.getForbiddenSize())) {
            // 触发 bigkey 终止
            logger.error("EasyCache BigKeyCheck: 缓存值大小超出拒绝存储, 预警大小={}, 缓存大小={}, key={}",
                    warnSizeString, realSizeString, key);
            if (bigKeyConfig.isForbiddenException()) {
                throw new CacheInterruptException(CacheConstants.CODE_ERROR_PARAM, "缓存大小超限:" + valSize);
            }
            return false;
        }
        if (valSize >= InnerSizeUtils.parseSize(bigKeyConfig.getWarnSize())) {
            logger.warn("EasyCache BigKeyCheck: 缓存值大小超出预警, 预警大小={}, 缓存大小={}, key={}",
                    warnSizeString, realSizeString, key);
        }
        return true;
    }

    /**
     * 元素数量校验
     *
     * @param key
     * @param valLen
     * @return
     */
    private boolean lenPass(String key, long valLen) {
        if (valLen >= bigKeyConfig.getForbiddenLen()) {
            // 触发 bigkey 终止
            logger.error("EasyCache BigKeyCheck: 缓存值大小超出拒绝存储, 预警大小={}, 缓存大小={}, key={}",
                    bigKeyConfig.getForbiddenLen(), valLen, key);
            if (bigKeyConfig.isForbiddenException()) {
                throw new CacheInterruptException(CacheConstants.CODE_ERROR_PARAM, "缓存大小超限:" + valLen);
            }
            return false;
        }
        if (valLen >= bigKeyConfig.getWarnLen()) {
            logger.warn("EasyCache BigKeyCheck: 缓存值大小超出预警, 预警大小={}, 缓存大小={}, key={}",
                    bigKeyConfig.getWarnLen(), valLen, key);
        }
        return true;
    }
}
