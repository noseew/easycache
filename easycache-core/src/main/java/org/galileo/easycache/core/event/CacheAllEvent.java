package org.galileo.easycache.core.event;

import org.galileo.easycache.core.filter.FilterContext;

public class CacheAllEvent extends OpEvent {

    public CacheAllEvent(FilterContext context) {
        super(context);
    }
}
