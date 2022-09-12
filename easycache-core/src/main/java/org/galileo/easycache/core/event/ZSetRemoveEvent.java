package org.galileo.easycache.core.event;

import org.galileo.easycache.core.filter.FilterContext;

public class ZSetRemoveEvent extends OpEvent {

    public ZSetRemoveEvent(FilterContext context) {
        super(context);
    }
}
