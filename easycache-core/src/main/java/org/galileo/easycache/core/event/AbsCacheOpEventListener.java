package org.galileo.easycache.core.event;

import java.util.Set;

public abstract class AbsCacheOpEventListener implements EventListener {

    public abstract Set<Class<? extends OpEvent>> getEventType();

    public boolean async() {
        return true;
    }

}
