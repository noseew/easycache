package org.galileo.easycache.core.event;

import org.galileo.easycache.core.filter.FilterContext;

public class CachePutEvent extends OpEvent {

    public CachePutEvent(FilterContext context) {
        super(context);
    }
}
