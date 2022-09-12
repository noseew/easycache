package org.galileo.easycache.core.event;

import org.apache.commons.lang3.StringUtils;
import org.galileo.easycache.core.filter.FilterContext;

/**
 * 抽象缓存操作事件, 用于内部使用
 * 每当有缓存操作, 则会触发该事件
 */
public abstract class OpEvent {

    protected long opMillis;

    protected final FilterContext context;

    protected OpEvent(FilterContext context) {
        this.context = context;
    }

    public FilterContext getContext() {
        return this.context;
    }

    public long getOpMillis() {
        return opMillis;
    }

    public void setOpMillis(long opMillis) {
        this.opMillis = opMillis;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("OpEvent: ").append(getClass().getName()).append(", key=");
        if (StringUtils.isNotEmpty(context.getKey())) {
            sb.append(context.getKey());
        } else {
            sb.append(context.getKeys().toString());
        }
        return sb.toString();
    }
}
