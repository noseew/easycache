package org.galileo.easycache.core.collect;

public interface Collector<D, R> {
    
    void collect(D data);
    
    R take();
}
