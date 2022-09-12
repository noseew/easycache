package org.galileo.easycache.core.filter;

import org.apache.commons.lang3.StringUtils;
import org.galileo.easycache.common.enums.CacheType;

/**
 * 延迟删除, TODO 删除量非常大的时候, 队列很容易满
 */
public class DelayRemoveFilter extends RemoveCompensateFilter {

    public DelayRemoveFilter(String namespace) {
        super(1000, 1, 5, "DelayRemoveFilter", namespace);
    }

    @Override
    public Object invoke(FilterContext context) {
        if (!CacheType.REMOTE.equals(context.getCache().getCacheType())) {
            return super.invoke(context);
        }
        Object invoke = super.invoke(context);
        Object key = StringUtils.isNotEmpty(context.getKey()) ? context.getKey() : context.getKeys();
        RemovedKey removedKey = new RemovedKey(key);
        taskHandler(removedKey);
        return invoke;
    }

    @Override
    protected boolean canProcess(FilterContext context) {
        return false;
    }
}
