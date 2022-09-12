package org.galileo.easycache.core.event;

import org.galileo.easycache.core.filter.FilterContext;

public class CacheGetEvent extends OpEvent {

    public CacheGetEvent(FilterContext context) {
        super(context);
    }
}
