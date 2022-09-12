package org.galileo.easycache.springboot.springdata;

import org.galileo.easycache.core.core.AbsExternalCache;
import org.galileo.easycache.core.core.config.NamespaceConfig;
import org.galileo.easycache.core.core.config.RemoteConfig;
import org.galileo.easycache.core.utils.InnerCodecUtils;
import org.springframework.data.redis.connection.Subscription;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class SpringDataCache extends AbsExternalCache {

    private final RedisTemplate<String, Object> redisTemplate;

    private final Set<String> subed = new ConcurrentSkipListSet<>();

    public SpringDataCache(RemoteConfig remoteConfig, RedisTemplate<String, Object> redisTemplate) {
        super(remoteConfig);
        this.redisTemplate = redisTemplate;
        this.cacheClientName = "SpringRedisTemplate";
    }

    @Override
    public byte[] doGet(byte[] key) {
        return redisTemplate.execute(c -> {
            return c.get(key);
        }, true);
    }

    @Override
    public void doPut(byte[] key, byte[] value, long expire) {
        redisTemplate.execute(c -> {
            return c.pSetEx(key, expire, value);
        }, true);

    }

    @Override
    public boolean doPutIfAbsent(byte[] key, byte[] value, long expire) {
        return redisTemplate.execute(c -> {
            Boolean setNX = c.setNX(key, value);
            if (setNX) {
                c.expire(key, expire);
            }
            return setNX;
        }, true);
    }

    @Override
    public boolean doRemove(byte[] key) {
        Long del = redisTemplate.execute(c -> {
            return c.del(key);
        }, true);
        return del != null && del > 0;
    }

    @Override
    public boolean doRemoveAll(byte[]... keys) {
        Long del = redisTemplate.execute(c -> {
            return c.del(keys);
        }, true);
        return del > 0;
    }

    @Override
    public void close() throws Exception {
        try {
            super.close();
            unSub(subed.toArray(new String[0]));
            closed.compareAndSet(false, true);
        } catch (Exception e) {
            // ignore
            logger.warn("", e);
        }
    }
    
    // pubsub

    @Override
    public void sub(Consumer<PubSubBody> consumer, String... channels) {
        byte[][] keyArray = new byte[channels.length][];
        int i = 0;
        for (String key : channels) {
            keyArray[i++] = InnerCodecUtils.encode(keySerial, key);
        }
        redisTemplate.execute(c -> {
            c.subscribe((msg, pattern) -> {
                // 此方法就是直接调用回调方法
                consumer.accept((PubSubBody) valueWrapperSerial.decoder().apply(msg.getBody()));
            }, keyArray);
            return null;
        }, true);
    }

    @Override
    public void unSub(String... channels) {
        byte[][] keyArray = new byte[channels.length][];
        int i = 0;
        for (String key : channels) {
            keyArray[i++] = InnerCodecUtils.encode(keySerial, key);
        }
        redisTemplate.execute(c -> {
            Subscription subscription = c.getSubscription();
            if (subscription != null) {
                subscription.unsubscribe(keyArray);
            }
            return null;
        }, true);
    }

    @Override
    public void doPub(String channel, PubSubBody msg) {
        redisTemplate.execute(c -> {
            c.publish(channel.getBytes(), valueWrapperSerial.encoder().apply(msg));
            return null;
        }, true);
    }
    
    // redis 操作

    @Override
    public void zadd(String key, long expire, double score, String value) {
        redisTemplate.opsForZSet().add(key, value, score);
        redisTemplate.expire(key, expire, TimeUnit.MILLISECONDS);
    }

    @Override
    public void zrem(String key, String... values) {
        redisTemplate.opsForZSet().remove(key, values);
    }

    @Override
    public void zremByScore(String key, double start, double end) {
        redisTemplate.opsForZSet().removeRangeByScore(key, start, end);
    }

    @Override
    public long zcount(String key, double start, double end) {
        return redisTemplate.opsForZSet().count(key, start, end);
    }

    @Override
    public long zrank(String key, String value) {
        return redisTemplate.opsForZSet().rank(key, value);
    }

    @Override
    public Set<String> zrange(String key, double start, double end) {
        Set<Object> range = redisTemplate.opsForZSet().range(key, (long) start, (long) end);
        // TODO 序列化
        return range.stream().map(e -> e.toString()).collect(Collectors.toSet());
    }

    @Override
    public void sadd(String key, long expire, String... members) {
        redisTemplate.opsForSet().add(key, members);
        redisTemplate.expire(key, expire, TimeUnit.MILLISECONDS);
    }

    @Override
    public void srem(String key, String... members) {
        redisTemplate.opsForSet().remove(key, members);
    }

    @Override
    public long scard(String key) {
        return redisTemplate.opsForSet().size(key);
    }

    @Override
    public String spop(String key) {
        Object pop = redisTemplate.opsForSet().pop(key);
        // TODO 序列化
        return pop != null ? pop.toString() : null;
    }

//    @Override
//    public Set<String> spop(String key, int count) {
//        List<Object> pops = redisTemplate.opsForSet().pop(key, count);
//        // TODO 序列化
//        return pops != null ? pops.stream().map(e -> e.toString()).collect(Collectors.toSet()) : new HashSet<>();
//    }
}
