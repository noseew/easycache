package org.galileo.easycache.core.filter;

import org.galileo.easycache.common.CacheProxy;
import org.galileo.easycache.core.utils.CacheContextUtils;

/**
 * 虚拟头结点filter, 是距离用户操作缓存组件最近的filter
 * 目前只有一个作用就是将上下文放到 ThreadLocal 中, 以便后面其他组件使用
 */
public class HeadFilter extends AbsInvokeFilter {

    public HeadFilter(CacheProxy target) {
        super("HeadFilter", null, target);
    }

    @Override
    public Object invoke(FilterContext context) {
        boolean isHead = CacheContextUtils.getFilterContext() == null;
        if (isHead) {
            CacheContextUtils.addFilterContext(context);
        }
        try {
            return super.invoke(context);
        } finally {
            if (isHead) {
                CacheContextUtils.removeFilterContext();
            }
        }
    }

    @Override
    protected boolean canProcess(FilterContext context) {
        return false;
    }

}
