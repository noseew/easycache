package org.galileo.easycache.common;

import org.galileo.easycache.common.enums.OpType;

import java.util.Set;

public interface CacheRedisClient {

    @FilterProxy(opType = OpType.ZSET_ADD)
    void zAdd(String cacheName, long expire, double score, @ValParam(paramType = String.class) String value);
    @FilterProxy(opType = OpType.ZSET_REM)
    void zRemove(String cacheName, @ValParam(paramType = String[].class) String... values);
    @FilterProxy(opType = OpType.ZSET_QUERY)
    void zRemoveByScore(String cacheName, double start, double end);
    @FilterProxy(opType = OpType.ZSET_QUERY)
    long zCount(String cacheName, double start, double end);
    @FilterProxy(opType = OpType.ZSET_QUERY)
    long zRank(String cacheName, @ValParam(paramType = String.class) String value);
    @FilterProxy(opType = OpType.ZSET_QUERY)
    Set<String> zRange(String cacheName, double start, double end);

    @FilterProxy(opType = OpType.SET_ADD)
    void sAdd(String cacheName, long expire, @ValParam(paramType = String[].class) String... members);
    @FilterProxy(opType = OpType.SET_REM)
    void sRemove(String cacheName, @ValParam(paramType = String[].class) String... members);
    @FilterProxy(opType = OpType.SET_QUERY)
    long sCard(String cacheName);
    @FilterProxy(opType = OpType.SET_REM)
    String sPop(String cacheName);
//    @FilterProxy(opType = OpType.SET_REM)
//    @Deprecated
//    Set<String> sPop(String cacheName, int count);
}
