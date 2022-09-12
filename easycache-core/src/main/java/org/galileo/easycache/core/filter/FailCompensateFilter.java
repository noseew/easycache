package org.galileo.easycache.core.filter;

import org.apache.commons.lang3.StringUtils;
import org.galileo.easycache.common.enums.CacheType;
import org.galileo.easycache.common.enums.OpType;

/**
 * 失败删除filter, 当缓存更新或删除失败后该补偿将起到作用
 */
public class FailCompensateFilter extends RemoveCompensateFilter {

    public FailCompensateFilter(String namespace) {
        super(1000, 3, 3, "FailCompensateFilter", namespace);
    }

    @Override
    public Object invoke(FilterContext context) {
        boolean canProcess = canProcess(context);
        if (!canProcess) {
            return super.invoke(context);
        }

        Object key = StringUtils.isNotEmpty(context.getKey()) ? context.getKey() : context.getKeys();

        try {
            Object invoke = super.invoke(context);
            successKey.put(key, context.getOpType());
            return invoke;
        } catch (Exception e) {
            RemovedKey removedKey = new RemovedKey(key);
            try {
                taskHandler(removedKey);
            } catch (Exception exception) {
                logger.warn("EasyCache 失败补偿调用报错 key={}", removedKey, exception);
            }
            throw e;
        }
    }

    @Override
    protected boolean canProcess(FilterContext context) {
        // 只有删除和新增才需要重试, 重试的方式都是删除, 因为新增失败可能导致的是更新失败, 所以重试使用删除
        if (CacheType.REMOTE.equals(context.getCache().getCacheType())) {
            return OpType.PUT.equals(context.getOpType()) || OpType.REMOVE.equals(context.getOpType());
        }
        return false;
    }
}
