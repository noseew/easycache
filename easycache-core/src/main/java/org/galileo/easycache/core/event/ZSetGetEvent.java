package org.galileo.easycache.core.event;

import org.galileo.easycache.core.filter.FilterContext;

public class ZSetGetEvent extends OpEvent {

    public ZSetGetEvent(FilterContext context) {
        super(context);
    }
}
