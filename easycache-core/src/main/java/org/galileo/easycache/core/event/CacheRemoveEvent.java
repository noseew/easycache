package org.galileo.easycache.core.event;

import org.galileo.easycache.core.filter.FilterContext;

public class CacheRemoveEvent extends OpEvent {

    public CacheRemoveEvent(FilterContext context) {
        super(context);
    }
}
