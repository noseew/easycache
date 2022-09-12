package org.galileo.easycache.core.filter;

import org.galileo.easycache.common.CacheProxy;
import org.galileo.easycache.common.ValWrapper;
import org.galileo.easycache.common.constants.CacheConstants;
import org.galileo.easycache.common.enums.OpType;
import org.galileo.easycache.core.exception.CacheInterruptException;

/**
 * 虚拟尾部过滤器, 是距离缓存组件最近的filter
 * 所以功能有
 * 1. 记录缓存组件操作时间, 
 * 2. 包装原始缓存组件异常, 并决定是否抛出该异常
 * 3. 保存缓存组件的操作结果包括状态
 */
public class TailFilter extends AbsInvokeFilter {

    public TailFilter(CacheProxy target) {
        super("TailFilter", null, target);
    }

    @Override
    public Object invoke(FilterContext context) {
        Object result = null;
        long start = System.currentTimeMillis();
        try {
            result = super.invoke(context);
        } catch (Exception ex) {
            logger.warn("EasyCache 实例调用异常 {}, ex", getTarget().getClass().getName(), ex);
            if (ex instanceof CacheInterruptException) {
                context.setResCode(((CacheInterruptException) ex).getCode());
                if (context.getResCode() == CacheConstants.CODE_ERROR_NET) {
                    logger.warn("EasyCache 实例调用异常: 组件异常, 向上抛出 {}", getTarget().getClass().getName());
                    throw ex;
                }
            } else {
                context.setResCode(CacheConstants.CODE_ERROR_OTHER);
            }
        } finally {
            context.setOpMillis(System.currentTimeMillis() - start);
        }
        if (context.getOpType() == OpType.GET && result != null) {
            if (context.getMethod().getReturnType() == ValWrapper.class) {
                context.setValWrapper((ValWrapper) result);
                context.setHit(true);
            }
        }
        return result;
    }

    @Override
    protected boolean canProcess(FilterContext context) {
        return false;
    }

}
