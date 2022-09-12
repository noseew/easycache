package org.galileo.easycache.core.event;

import org.galileo.easycache.core.filter.FilterContext;

public class SetRemoveEvent extends OpEvent {

    public SetRemoveEvent(FilterContext context) {
        super(context);
    }
}
