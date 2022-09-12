package org.galileo.easycache.core.event;


import org.galileo.easycache.core.filter.FilterContext;

public class ZSetPutEvent extends OpEvent {

    public ZSetPutEvent(FilterContext context) {
        super(context);
    }
}
