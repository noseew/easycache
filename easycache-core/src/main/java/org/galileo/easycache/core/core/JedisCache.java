package org.galileo.easycache.core.core;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.galileo.easycache.common.SerialPolicy;
import org.galileo.easycache.core.core.config.RemoteConfig;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPubSub;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class JedisCache extends AbsExternalCache {

    private JedisPool jedisPool;

    private final Map<String, UJedisPubSub> subed = new ConcurrentHashMap<>();

    public JedisCache(RemoteConfig remoteConfig) {
        super(remoteConfig);
        this.cacheClientName = "JedisCache";
    }

    @Override
    public byte[] doGet(byte[] key) {
        Jedis jedis = getJedis();
        try {
            byte[] val = jedis.get(key);
            return val;
        } finally {
            releaseJedis(jedis);
        }
    }

    @Override
    public void doPut(byte[] key, byte[] value, long expire) {
        Jedis jedis = getJedis();
        try {
            jedis.setex(key, (int) (expire / 1000), value);
        } finally {
            releaseJedis(jedis);
        }
    }

    @Override
    public boolean doPutIfAbsent(byte[] key, byte[] value, long expire) {
        Jedis jedis = getJedis();
        try {
            boolean setNx = jedis.setnx(key, value) > 0;
            if (setNx) {
                jedis.expire(key, (int) (expire / 1000));
            }
            return setNx;
        } finally {
            releaseJedis(jedis);
        }
    }

    @Override
    public boolean doRemove(byte[] key) {
        Jedis jedis = getJedis();
        try {
            return jedis.del(key) > 0;
        } finally {
            releaseJedis(jedis);
        }
    }

    @Override
    public boolean doRemoveAll(byte[]... keys) {
        Jedis jedis = getJedis();
        try {
            return jedis.del(keys) > 0;
        } finally {
            releaseJedis(jedis);
        }
    }

    private Jedis getJedis() {
        if (jedisPool == null) {
            initPool();
        }
        try {
            return jedisPool.getResource();
        } catch (Exception e) {
            if (closed.get()) {
                logger.error("EasyCache getJedis error: ", e);
                return null;
            } else {
                initPool();
            }
        }
        return jedisPool.getResource();
    }

    private void releaseJedis(Jedis jedis) {
        try {
            if (jedis != null) {
                jedis.close();
            }
        } catch (Exception e) {
            logger.warn("EasyCache releaseJedis error ", e);
        }
    }

    private synchronized void initPool() {
        GenericObjectPoolConfig poolConfig =  new GenericObjectPoolConfig<>();
        RemoteConfig.Pool pool = remoteConfig.getJedis().getPool();
        poolConfig.setMaxIdle(pool.getMaxIdle());
        poolConfig.setMinIdle(pool.getMinIdle());
        poolConfig.setMaxTotal(pool.getMaxActive());
        // 业务量很大时候建议设置为false，减少一次ping的开销。
        // 向资源池借用连接时是否做连接有效性检测（ping）。检测到的无效连接将会被移除。
//        poolConfig.setTestOnBorrow(true);
        // 向资源池归还连接时是否做连接有效性检测（ping）。检测到无效连接将会被移除。
//        poolConfig.setTestOnReturn(true);
        
        jedisPool = new JedisPool(poolConfig, remoteConfig.getHost(), remoteConfig.getPort());
    }

    @Override
    public void close() throws Exception {
        try {
            super.close();
            unSub(subed.keySet().toArray(new String[0]));
            jedisPool.close();
            closed.compareAndSet(false, true);
        } catch (Exception e) {
            // ignore
            logger.warn("", e);
        }
    }
    
    // pubsub

    @Override
    public void sub(Consumer<PubSubBody> consumer, String... channels) {
        UJedisPubSub jedisPubSub = new UJedisPubSub(consumer, valueWrapperSerial);
        for (String channel : channels) {
            subed.put(channel, jedisPubSub);
        }
        Jedis jedis = getJedis();
        try {
            if (jedis != null) {
                jedis.subscribe(jedisPubSub, channels);
            }
        } catch (Exception e) {
            logger.error("EasyCache subscribe error ", e);
            throw e;
        } finally {
            releaseJedis(jedis);
        }
    }

    @Override
    public void unSub(String... channels) {
        Set<UJedisPubSub> unsubed = new HashSet<>();
        for (String channel : channels) {
            UJedisPubSub jedisPubSub = subed.remove(channel);
            if (jedisPubSub != null && !unsubed.contains(jedisPubSub)) {
                jedisPubSub.unsubscribe(channels);
                unsubed.add(jedisPubSub);
            }
        }
    }

    @Override
    public void doPub(String channel, PubSubBody msg) {
        Jedis jedis = getJedis();
        try {
            if (jedis != null) {
                jedis.publish(keySerial.encoder().apply(channel), valueWrapperSerial.encoder().apply(msg));
            }
        } catch (Exception e) {
            logger.error("EasyCache doPub error ", e);
            throw e;
        } finally {
            releaseJedis(jedis);
        }
    }

    // redis 客户端操作
    
    @Override
    public void zadd(String key, long expire, double score, String value) {
        Jedis jedis = getJedis();
        try {
            if (jedis != null) {
                jedis.zadd(key, score, value);
                jedis.expire(key, (int) (expire / 1000L));
            }
        } catch (Exception e) {
            logger.error("EasyCache zadd error ", e);
            throw e;
        } finally {
            releaseJedis(jedis);
        }
    }

    @Override
    public void zrem(String key, String... values) {
        Jedis jedis = getJedis();
        try {
            if (jedis != null) {
                jedis.zrem(key, values);
            }
        } catch (Exception e) {
            logger.error("EasyCache zrem error ", e);
            throw e;
        } finally {
            releaseJedis(jedis);
        }
    }

    @Override
    public void zremByScore(String key, double start, double end) {
        Jedis jedis = getJedis();
        try {
            if (jedis != null) {
                jedis.zremrangeByScore(key, start, end);
            }
        } catch (Exception e) {
            logger.error("EasyCache zremByScore error ", e);
            throw e;
        } finally {
            releaseJedis(jedis);
        }
    }

    @Override
    public long zcount(String key, double start, double end) {
        Jedis jedis = getJedis();
        try {
            if (jedis != null) {
                return jedis.zcount(key, start, end);
            }
        } catch (Exception e) {
            logger.error("EasyCache zcount error ", e);
            throw e;
        } finally {
            releaseJedis(jedis);
        }
        return 0;
    }

    @Override
    public long zrank(String key, String value) {
        Jedis jedis = getJedis();
        try {
            if (jedis != null) {
                return jedis.zrank(key, value);
            }
        } catch (Exception e) {
            logger.error("EasyCache zrank error ", e);
            throw e;
        } finally {
            releaseJedis(jedis);
        }
        return 0;
    }

    @Override
    public Set<String> zrange(String key, double start, double end) {
        Jedis jedis = getJedis();
        try {
            if (jedis != null) {
                return jedis.zrange(key, (long) start, (long) end);
            }
        } catch (Exception e) {
            logger.error("EasyCache zrange error ", e);
            throw e;
        } finally {
            releaseJedis(jedis);
        }
        return null;
    }

    @Override
    public void sadd(String key, long expire, String... members) {
        Jedis jedis = getJedis();
        try {
            if (jedis != null) {
                jedis.sadd(key, members);
                jedis.expire(key, (int) (expire / 1000L));
            }
        } catch (Exception e) {
            logger.error("EasyCache sadd error ", e);
            throw e;
        } finally {
            releaseJedis(jedis);
        }
    }

    @Override
    public void srem(String key, String... members) {
        Jedis jedis = getJedis();
        try {
            if (jedis != null) {
                jedis.srem(key, members);
            }
        } catch (Exception e) {
            logger.error("EasyCache srem error ", e);
            throw e;
        } finally {
            releaseJedis(jedis);
        }
    }

    @Override
    public long scard(String key) {
        Jedis jedis = getJedis();
        try {
            if (jedis != null) {
                return jedis.scard(key);
            }
        } catch (Exception e) {
            logger.error("EasyCache scard error ", e);
            throw e;
        } finally {
            releaseJedis(jedis);
        }
        return 0;
    }

    @Override
    public String spop(String key) {
        Jedis jedis = getJedis();
        try {
            if (jedis != null) {
                return jedis.spop(key);
            }
        } catch (Exception e) {
            logger.error("EasyCache spop error ", e);
            throw e;
        } finally {
            releaseJedis(jedis);
        }
        return null;
    }

//    @Override
//    public Set<String> spop(String key, int count) {
//        Jedis jedis = getJedis();
//        try {
//            if (jedis != null) {
//                return jedis.spop(key, count);
//            }
//        } catch (Exception e) {
//            logger.error("EasyCache spop error ", e);
//            throw e;
//        } finally {
//            releaseJedis(jedis);
//        }
//        return null;
//    }

    static class UJedisPubSub extends JedisPubSub {

        Consumer<PubSubBody> consumer;
        SerialPolicy valueWrapperSerial;

        public UJedisPubSub(Consumer<PubSubBody> consumer, SerialPolicy valueWrapperSerial) {
            this.consumer = consumer;
            this.valueWrapperSerial = valueWrapperSerial;
        }

        @Override
        public void onMessage(String channel, String message) {
            consumer.accept((PubSubBody) valueWrapperSerial.decoder().apply(message.getBytes()));
        }
    }
}
