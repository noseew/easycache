package org.galileo.easycache.core.event;


import org.galileo.easycache.common.enums.OpType;
import org.galileo.easycache.core.core.EasyCacheManager;
import org.galileo.easycache.core.filter.AbsInvokeFilter;
import org.galileo.easycache.core.filter.FilterContext;

import java.util.List;
import java.util.Set;
import java.util.concurrent.Executor;

/**
 * 缓存操作事件发布器
 * 事件不能影响正常的缓存操作流程, 仅仅起到触发通知的作用
 */
public class CacheOpEventPublisher extends AbsInvokeFilter {

    private Executor executor;

    public CacheOpEventPublisher(String namespace) {
        super("CacheOpEventPublisher", namespace, null);
    }

    public void setExecutor(Executor executor) {
        this.executor = executor;
    }

    @Override
    public Object invoke(FilterContext context) {

        Object result = super.invoke(context);
        List<EventListener> eventListenerList = EasyCacheManager.getEventListenerList();
        if (eventListenerList.isEmpty()) {
            return result;
        }
        OpEvent event = null;
        if (context.getOpType().isCache()) {
            event = buildCacheEvent(context);
        } else if (context.getOpType().isSet()) {
            event = buildSetEvent(context);
        } else if (context.getOpType().isZSet()) {
            event = buildZSetEvent(context);
        }
        if (event == null) {
            return result;
        }
        event.setOpMillis(context.getOpMillis());

        for (EventListener listener : eventListenerList) {
            if (listener instanceof AbsCacheOpEventListener) {
                publish(event, listener);
            }
        }
        return result;
    }

    @Override
    protected boolean canProcess(FilterContext context) {
        return false;
    }

    private OpEvent buildCacheEvent(FilterContext context) {
        if (context.getOpType().equals(OpType.GET)) {
            return new CacheGetEvent(context);
        }
        if (context.getOpType().equals(OpType.PUT)) {
            return new CachePutEvent(context);
        }
        if (context.getOpType().equals(OpType.REMOVE)) {
            return new CacheRemoveEvent(context);
        }
        return null;
    }

    private OpEvent buildSetEvent(FilterContext context) {
        if (context.getOpType().equals(OpType.SET_QUERY)) {
            return new SetGetEvent(context);
        }
        if (context.getOpType().equals(OpType.SET_ADD)) {
            return new SetPutEvent(context);
        }
        if (context.getOpType().equals(OpType.SET_REM)) {
            return new SetRemoveEvent(context);
        }
        return null;
    }

    private OpEvent buildZSetEvent(FilterContext context) {
        if (context.getOpType().equals(OpType.ZSET_QUERY)) {
            return new ZSetGetEvent(context);
        }
        if (context.getOpType().equals(OpType.ZSET_ADD)) {
            return new ZSetGetEvent(context);
        }
        if (context.getOpType().equals(OpType.ZSET_REM)) {
            return new ZSetRemoveEvent(context);
        }
        return null;
    }

    private void publish(OpEvent event, EventListener listener) {
        AbsCacheOpEventListener absListener = (AbsCacheOpEventListener) listener;
        Set<Class<? extends OpEvent>> listenTypeSet = absListener.getEventType();
        try {
            if (listenTypeSet.contains(event.getClass()) || listenTypeSet.contains(CacheAllEvent.class)) {
                if (executor != null && absListener.async()) {
                    OpEvent finalEvent = event;
                    executor.execute(() -> listener.on(finalEvent));
                } else {
                    listener.on(event);
                }
            }
        } catch (Exception e) {
            // IGNORE
            logger.warn("", e);
        }
    }

}
