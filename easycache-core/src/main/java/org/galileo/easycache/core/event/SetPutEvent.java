package org.galileo.easycache.core.event;

import org.galileo.easycache.core.filter.FilterContext;

public class SetPutEvent extends OpEvent {

    public SetPutEvent(FilterContext context) {
        super(context);
    }
}
