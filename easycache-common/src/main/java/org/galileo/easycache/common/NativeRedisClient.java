package org.galileo.easycache.common;

import java.util.Set;

public interface NativeRedisClient {

    boolean del(String key);
    boolean delAll(Set<String> keys);

    void zadd(String key, long expire, double score, String value);
    void zrem(String key, String... values);
    void zremByScore(String key, double start, double end);
    long zcount(String key, double start, double end);
    long zrank(String key, String value);
    Set<String> zrange(String key, double start, double end);

    void sadd(String key, long expire, String... members);
    void srem(String key, String... members);
    long scard(String key);
    String spop(String key);
//    @Deprecated
//    Set<String> spop(String key, int count);
}
