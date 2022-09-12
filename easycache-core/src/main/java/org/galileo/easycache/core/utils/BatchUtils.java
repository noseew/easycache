package org.galileo.easycache.core.utils;

import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

public class BatchUtils {

    private BatchUtils() {

    }

    public static final int maxSize = 500;

    public static <T> boolean batchList(List<T> list, Function<Collection<T>, Boolean> function) {
        if (CollectionUtils.isEmpty(list) || function == null) {
            return false;
        }
        if (list.size() <= maxSize) {
            return function.apply(list);
        }
        List<List<T>> partition = Lists.partition(list, maxSize);
        return partition.stream().map(function).reduce((e1, e2) -> e1 && e2).orElse(false);
    }

    public static <T> void batchList(List<T> list, Consumer<Collection<T>> consumer) {
        if (CollectionUtils.isEmpty(list) || consumer == null) {
            return;
        }
        if (list.size() <= maxSize) {
            consumer.accept(list);
            return;
        }
        List<List<T>> partition = Lists.partition(list, maxSize);
        partition.forEach(consumer);
    }

    public static <T> boolean batchSet(Set<T> set, Function<Collection<T>, Boolean> function) {
        if (CollectionUtils.isEmpty(set) || function == null) {
            return false;
        }
        if (set.size() <= maxSize) {
            return function.apply(set);
        }
        return batchList(Lists.newArrayList(set), function);
    }

    public static <T> void batchSet(Set<T> set, Consumer<Collection<T>> consumer) {
        if (CollectionUtils.isEmpty(set) || consumer == null) {
            return;
        }
        if (set.size() <= maxSize) {
            consumer.accept(set);
            return;
        }
        batchList(Lists.newArrayList(set), consumer);
    }
}
