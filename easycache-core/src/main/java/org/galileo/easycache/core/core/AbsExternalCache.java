package org.galileo.easycache.core.core;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.galileo.easycache.common.*;
import org.galileo.easycache.common.constants.CacheConstants;
import org.galileo.easycache.common.enums.CacheType;
import org.galileo.easycache.common.enums.OpType;
import org.galileo.easycache.core.core.config.NamespaceConfig;
import org.galileo.easycache.core.core.config.RemoteConfig;
import org.galileo.easycache.core.event.PubSub;
import org.galileo.easycache.core.exception.CacheInterruptException;
import org.galileo.easycache.core.utils.BatchUtils;
import org.galileo.easycache.core.utils.InnerAssertUtils;
import org.galileo.easycache.core.utils.InnerCodecUtils;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

/**
 * 外部缓存实例, 需要序列化
 */
public abstract class AbsExternalCache extends AbsCache implements PubSub, NativeRedisClient, CacheRedisClient {

    protected final SerialPolicy keySerial;
    protected final SerialPolicy valueWrapperSerial;
    protected final SerialPolicy valueCompressSerial;
    protected final SerialPolicy valueSerial;
    protected RemoteConfig remoteConfig;

    private final static ScheduledThreadPoolExecutor batchPubScheduled;
    private final Map<String, Set<String>> batchPubQueue = new ConcurrentHashMap<>();

    protected Map<String, Consumer<PubSubBody>> consumerMap = new ConcurrentHashMap<>();

    static {
        batchPubScheduled = new ScheduledThreadPoolExecutor(1, new ThreadFactoryBuilder().setDaemon(true).setNameFormat("batchPubScheduled-").build());
    }

    protected AbsExternalCache(RemoteConfig remoteConfig) {
        super(remoteConfig.getParent(), CacheType.REMOTE);
        this.remoteConfig = remoteConfig;
        keySerial = EasyCacheManager
                .getSerialPolicy(remoteConfig.getKeySerialName(), EasyCacheManager.getSerialPolicy(SerialPolicy.STRING, null));
        valueWrapperSerial = EasyCacheManager.getSerialPolicy(remoteConfig.getValueWrapperSerialName(), EasyCacheManager
                .getSerialPolicy(SerialPolicy.Jackson, null));
        valueCompressSerial = EasyCacheManager.getSerialPolicy(remoteConfig.getValueCompressSerialName(), EasyCacheManager
                .getSerialPolicy(SerialPolicy.Gzip, null));
        valueSerial = EasyCacheManager
                .getSerialPolicy(remoteConfig.getValueSerialName(), EasyCacheManager.getSerialPolicy(SerialPolicy.Jackson, null));
        this.cacheLock = new RedisCacheLock(this);
    }

    @Override
    public void init() {
        if (init.compareAndSet(false, true)) {
            startSub();
            startBatchPubTask();
        }
    }

    @Override
    public void close() throws Exception {
        try {
            if (batchPubScheduled != null) {
                batchPubScheduled.awaitTermination(1500, TimeUnit.MILLISECONDS);
            }
        } catch (Exception e) {
            // ignore
            logger.warn("", e);
        }
    }

    @Override
    @FilterProxy(opType = OpType.GET)
    public ValWrapper get(String key) {
        byte[] val = null;
        try {
            val = doGet(InnerCodecUtils.encode(keySerial, key));
        } catch (Exception e) {
            logger.error("EasyCache doGet error, key={}", key, e);
            throw new CacheInterruptException(CacheConstants.CODE_ERROR_NET,
                    "doGet error, key=" + key + e.getMessage());
        }
        if (val == null) {
            return null;
        }
        ValWrapper valWrapper = (ValWrapper) InnerCodecUtils.decode(valueWrapperSerial, val);
        valWrapper.setValue(deSerialVal(valWrapper));
        return valWrapper;
    }

    public abstract byte[] doGet(byte[] key);

    @Override
    @FilterProxy(opType = OpType.PUT)
    public <K, V> void put(String key, @ValParam(paramType = ValWrapper.class) ValWrapper valWrapper) {
        InnerAssertUtils.notNull(valWrapper, "'valWrapper' can not be null");
        boolean cacheNullValue = namespaceConfig.getParent().getPierceDefend().isCacheNullValue();
        if (!cacheNullValue && valWrapper.getValue() == null) {
            return;
        }
        try {
            valWrapper.setValue(serialVal(valWrapper));
            long expire = valWrapper.getRealExpireTs() - System.currentTimeMillis();
            doPut(InnerCodecUtils.encode(keySerial, key), InnerCodecUtils.encode(valueWrapperSerial, valWrapper),
                    expire);
        } catch (Exception e) {
            logger.error("EasyCache doPut error, key={}", key, e);
            throw new CacheInterruptException(CacheConstants.CODE_ERROR_NET,
                    "doPut error, key=" + key + e.getMessage());
        }
    }

    public abstract void doPut(byte[] key, byte[] value, long expire);

    @Override
    @FilterProxy(opType = OpType.PUT)
    public <K, V> boolean putIfAbsent(String key, @ValParam(paramType = ValWrapper.class) ValWrapper valWrapper) {
        InnerAssertUtils.notNull(valWrapper, "'valWrapper' can not be null");
        boolean cacheNullValue = namespaceConfig.getParent().getPierceDefend().isCacheNullValue();
        if (!cacheNullValue && valWrapper.getValue() == null) {
            return false;
        }
        try {
            valWrapper.setValue(serialVal(valWrapper));
            long expire = valWrapper.getRealExpireTs() - System.currentTimeMillis();
            return this.doPutIfAbsent(InnerCodecUtils.encode(keySerial, key),
                    InnerCodecUtils.encode(valueWrapperSerial, valWrapper), expire);
        } catch (Exception e) {
            logger.error("EasyCache doPutIfAbsent error, key={}", key, e);
            throw new CacheInterruptException(CacheConstants.CODE_ERROR_NET,
                    "doPutIfAbsent error, key=" + key + e.getMessage());
        }
    }

    public abstract boolean doPutIfAbsent(byte[] key, byte[] value, long expire);

    @Override
    @FilterProxy(opType = OpType.REMOVE)
    public <K> boolean remove(String key) {
        try {
            return this.doRemove(InnerCodecUtils.encode(keySerial, key));
        } catch (Exception e) {
            logger.error("EasyCache doRemove error, key={}", key, e);
            throw new CacheInterruptException(CacheConstants.CODE_ERROR_NET,
                    "doRemove error, key=" + key + e.getMessage());
        } finally {
            if (!key.endsWith(CacheLock.LOCK_POSTFIX)) {
                PubSubBody body = new PubSubBody();
                body.getKeys().add(key);
                body.setChannel(keyRemoveTopic);
                pub(keyRemoveTopic, body);
            }
        }
    }

    public abstract boolean doRemove(byte[] key);

    @Override
    @FilterProxy(opType = OpType.REMOVE)
    public <K> boolean removeAll(Set<String> keys) {
        byte[][] keyArray = new byte[keys.size()][];
        int i = 0;
        for (String key : keys) {
            keyArray[i++] = InnerCodecUtils.encode(keySerial, key);
        }
        try {
            if (keyArray.length > BatchUtils.maxSize) {
                logger.warn("EasyCache removeAll 单次操作数量太多 {}", keys.size());
                return BatchUtils.batchList(Lists.newArrayList(keyArray), e -> {
                    return this.doRemoveAll(e.toArray(new byte[0][]));
                });
            }
            return this.doRemoveAll(keyArray);
        } catch (Exception e) {
            logger.error("EasyCache doRemoveAll error, key={}", keys, e);
            throw new CacheInterruptException(CacheConstants.CODE_ERROR_NET,
                    "doRemoveAll error, key=" + keys + e.getMessage());
        } finally {
            PubSubBody body = new PubSubBody();
            body.getKeys().addAll(keys);
            body.setChannel(keyRemoveTopic);
            pub(keyRemoveTopic, body);
        }
    }

    public abstract boolean doRemoveAll(byte[]... keys);

    @Override
    public Object deSerialVal(ValWrapper valWrapper) {
        return InnerCodecUtils.deSerialVal(valWrapper);
    }

    @Override
    public Object serialVal(ValWrapper valWrapper) {
        return InnerCodecUtils.serialVal(valWrapper, this.valueSerial, this.valueCompressSerial, namespaceConfig.getCompressThreshold());
    }

    private void startBatchPubTask() {
        batchPubScheduled.scheduleAtFixedRate(() -> {
                    if (batchPubQueue.isEmpty() || closed.get()) {
                        return;
                    }
                    for (String channelType : batchPubQueue.keySet()) {
                        Set<String> keys = batchPubQueue.remove(channelType);
                        PubSubBody msg = new PubSubBody();
                        String[] split = channelType.split(channelTypeSplit);
                        msg.setType(Integer.parseInt(split[1]));
                        msg.setChannel(split[0]);
                        msg.getKeys().addAll(keys);
                        doPub(msg.getChannel(), msg);
                    }
                }, remoteConfig.getBatchPubTime().toMillis(),
                remoteConfig.getBatchPubTime().toMillis(),
                TimeUnit.MILLISECONDS);
    }

    /**
     * pub操作进行优化, 可以通过合并批量pub来提升性能
     *
     * @param channel
     * @param msg
     */
    @Override
    public void pub(String channel, PubSubBody msg) {
        if (!remoteConfig.isEnabledPubsub()) {
            return;
        }
        AtomicInteger waitPubSize = new AtomicInteger();
        if (!batchPubQueue.isEmpty()) {
            batchPubQueue.forEach((channelType, keys) -> waitPubSize.addAndGet(keys.size()));
        }
        if (waitPubSize.get() >= remoteConfig.getBatchPubSize()
                || msg.getKeys().size() >= remoteConfig.getBatchPubSize()) {
            // 待发送队列有点大, 直接pub
            doPub(channel, msg);
        } else if (!remoteConfig.isBatchPub()) {
            // 指定了直接pub
            doPub(channel, msg);
        } else {
            Set<String> keys = batchPubQueue.computeIfAbsent(msg.getChannel() + channelTypeSplit + msg.getType(),
                    k -> new CopyOnWriteArraySet<>());
            keys.addAll(msg.getKeys());
        }
    }

    protected abstract void doPub(String channel, PubSubBody msg);

    public void addRemoveConsumer(Consumer<PubSubBody> consumer) {
        logger.debug("EasyCache AbsExternalCache 添加1名订阅者, 监听 '{}'", PubSub.TypeRemoveKey);
        consumerMap.put(keyRemoveTopic, consumer);
    }

    private void startSub() {
        if (!remoteConfig.isEnabledPubsub()) {
            return;
        }
        logger.debug("EasyCache AbsExternalCache 开始订阅");

        // 异步订阅
        Thread thread = subscribeThread(() -> {
            this.sub(e -> {
                consumerMap.forEach((channel, consumer) -> {
                    consumer.accept(e);
                });
            }, keyRemoveTopic);
        });
        thread.start();
    }

    protected Thread subscribeThread(Runnable runnable) {
        return new ThreadFactoryBuilder().setDaemon(true).setNameFormat("easycache-redis-subscribe-" + getCacheInfo()).build()
                .newThread(runnable);
    }

    @Override
    public NativeRedisClient getNativeRedisClient() {
        return this;
    }

    @Override
    public CacheRedisClient getCacheRedisClient() {
        return (CacheRedisClient) this.proxy;
    }

    @Override
    public boolean del(String key) {
        return remove(key);
    }

    @Override
    public boolean delAll(Set<String> keys) {
        return removeAll(keys);
    }

    @Override
    @FilterProxy(opType = OpType.ZSET_ADD)
    public void zAdd(String cacheName, long expire, double score, @ValParam(paramType = String.class) String value) {
        zadd(cacheName, expire, score, value);
    }

    @Override
    @FilterProxy(opType = OpType.ZSET_REM)
    public void zRemove(String cacheName, @ValParam(paramType = String[].class) String... values) {
        zrem(cacheName, values);
    }

    @Override
    @FilterProxy(opType = OpType.ZSET_QUERY)
    public void zRemoveByScore(String cacheName, double start, double end) {
        zremByScore(cacheName, start, end);
    }

    @Override
    @FilterProxy(opType = OpType.ZSET_QUERY)
    public long zCount(String cacheName, double start, double end) {
        return zcount(cacheName, start, end);
    }

    @Override
    @FilterProxy(opType = OpType.ZSET_QUERY)
    public long zRank(String cacheName, @ValParam(paramType = String.class) String value) {
        return zrank(cacheName, value);
    }

    @Override
    @FilterProxy(opType = OpType.ZSET_QUERY)
    public Set<String> zRange(String cacheName, double start, double end) {
        return zrange(cacheName, start, end);
    }

    @Override
    @FilterProxy(opType = OpType.SET_ADD)
    public void sAdd(String cacheName, long expire, @ValParam(paramType = String[].class) String... members) {
        sadd(cacheName, expire, members);
    }

    @Override
    @FilterProxy(opType = OpType.SET_REM)
    public void sRemove(String cacheName, @ValParam(paramType = String[].class) String... members) {
        srem(cacheName, members);
    }

    @Override
    @FilterProxy(opType = OpType.SET_QUERY)
    public long sCard(String cacheName) {
        return scard(cacheName);
    }

    @Override
    @FilterProxy(opType = OpType.SET_REM)
    public String sPop(String cacheName) {
        return spop(cacheName);
    }

//    @Override
//    @FilterProxy(opType = OpType.SET_REM)
//    public Set<String> sPop(String cacheName, int count) {
//        return spop(cacheName, count);
//    }

}
