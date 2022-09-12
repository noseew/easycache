package org.galileo.easycache.core.event;

/**
 * 缓存时间监听器, 用于监听缓存操作的各种事件
 */
public interface EventListener {
    void on(OpEvent event);
}
