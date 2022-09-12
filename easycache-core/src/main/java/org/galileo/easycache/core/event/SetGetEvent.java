package org.galileo.easycache.core.event;

import org.galileo.easycache.core.filter.FilterContext;

public class SetGetEvent extends OpEvent {

    public SetGetEvent(FilterContext context) {
        super(context);
    }
}
