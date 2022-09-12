package org.galileo.easycache.core.core.config;


import org.galileo.easycache.common.SerialPolicy;
import org.galileo.easycache.common.constants.CacheConstants;
import org.galileo.easycache.common.enums.ConsistencyType;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

public class EasyCacheConfig extends InheritableConfig<EasyCacheConfig> {
    /**
     * 全局开关
     */
    private boolean enabled = CacheConstants.DEFAULT_ENABLED;
    /**
     * 应用名
     */
    private String appName = CacheConstants.DEFAULT;
    /**
     * 数据表示
     */
    private String trafficTag;
    /**
     * 默认过期时间
     */
    private Duration expire = Duration.ofMillis(CacheConstants.DEFAULT_EXPIRE);
    /**
     * 击穿防护
     */
    private BreakdownDefend breakdownDefend = new BreakdownDefend();
    /**
     * 穿透防护
     */
    private PierceDefend pierceDefend = new PierceDefend();
    /**
     * 一致性策略
     */
    private String consistency = ConsistencyType.EVENTUAL.getVal();
    /**
     * debug调试日志
     */
    private boolean debug = false;
    /**
     * 开启Pubsub功能
     */
    private boolean enabledPubsub = true;
    /**
     * 批量Pub优化
     */
    private boolean batchPub = true;
    /**
     * 批量Pub数量
     */
    private int batchPubSize = 500;
    /**
     * 批量Pub时间, 单位毫秒
     */
    private Duration batchPubTime = Duration.ofMillis(200);

    /**
     * 大缓存配置
     */
    private BigKeyConfig bigKey = new BigKeyConfig();
    /**
     * 缓存降级
     */
    private CircuitBreakerConfig circuitBreaker = new CircuitBreakerConfig();

    /**
     * 远程缓存配置
     */
    private Map<String, NamespaceConfig> ns = new HashMap<>();
    /**
     * 本地缓存配置
     */
    private InternalConfig local = new InternalConfig();

    /**
     * 缓存key 序列化方式
     */
    private String keySerialName = SerialPolicy.STRING;
    /**
     * 缓存包装类 压缩方式
     */
    private String valueWrapperSerialName = SerialPolicy.Jackson;
    /**
     * 缓存存储值 压缩方式
     */
    private String valueCompressSerialName = SerialPolicy.Gzip;
    /**
     * 缓存存储值 序列化方式
     */
    private String valueSerialName = SerialPolicy.Jackson;
    /**
     * 压缩阈值, 单位字节
     */
    private int compressThreshold = CacheConstants.COMPRESS_THRESHOLD;

    /**
     * 导出相关配置
     */
    private CacheReporterConfig cacheReporter = new CacheReporterConfig();

    public BreakdownDefend getBreakdownDefend() {
        return breakdownDefend;
    }

    public void setBreakdownDefend(BreakdownDefend breakdownDefend) {
        this.breakdownDefend = breakdownDefend;
    }

    @Override
    public String getConsistency() {
        return consistency;
    }

    @Override
    public void setConsistency(String consistency) {
        this.consistency = consistency;
    }

    public CircuitBreakerConfig getCircuitBreaker() {
        return circuitBreaker;
    }

    public void setCircuitBreaker(CircuitBreakerConfig circuitBreaker) {
        this.circuitBreaker = circuitBreaker;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public Duration getExpire() {
        return expire;
    }

    @Override
    public void setExpire(Duration expire) {
        this.expire = expire;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getTrafficTag() {
        return trafficTag;
    }

    public void setTrafficTag(String trafficTag) {
        this.trafficTag = trafficTag;
    }

    public Map<String, NamespaceConfig> getNs() {
        return ns;
    }

    public void setNs(Map<String, NamespaceConfig> ns) {
        this.ns = ns;
    }

    @Override
    public String getKeySerialName() {
        return keySerialName;
    }

    @Override
    public void setKeySerialName(String keySerialName) {
        this.keySerialName = keySerialName;
    }

    @Override
    public String getValueWrapperSerialName() {
        return valueWrapperSerialName;
    }

    @Override
    public void setValueWrapperSerialName(String valueWrapperSerialName) {
        this.valueWrapperSerialName = valueWrapperSerialName;
    }

    @Override
    public String getValueCompressSerialName() {
        return valueCompressSerialName;
    }

    @Override
    public void setValueCompressSerialName(String valueCompressSerialName) {
        this.valueCompressSerialName = valueCompressSerialName;
    }

    @Override
    public String getValueSerialName() {
        return valueSerialName;
    }

    @Override
    public void setValueSerialName(String valueSerialName) {
        this.valueSerialName = valueSerialName;
    }

    @Override
    public int getCompressThreshold() {
        return compressThreshold;
    }

    @Override
    public void setCompressThreshold(int compressThreshold) {
        this.compressThreshold = compressThreshold;
    }

    public BigKeyConfig getBigKey() {
        return bigKey;
    }

    public void setBigKey(BigKeyConfig bigKey) {
        this.bigKey = bigKey;
    }

    public InternalConfig getLocal() {
        return local;
    }

    public void setLocal(InternalConfig local) {
        this.local = local;
    }

    public PierceDefend getPierceDefend() {
        return pierceDefend;
    }

    public void setPierceDefend(PierceDefend pierceDefend) {
        this.pierceDefend = pierceDefend;
    }

    public boolean isDebug() {
        return debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public boolean isBatchPub() {
        return batchPub;
    }

    public void setBatchPub(boolean batchPub) {
        this.batchPub = batchPub;
    }

    public Duration getBatchPubTime() {
        return batchPubTime;
    }

    public void setBatchPubTime(Duration batchPubTime) {
        this.batchPubTime = batchPubTime;
    }

    public int getBatchPubSize() {
        return batchPubSize;
    }

    public void setBatchPubSize(int batchPubSize) {
        this.batchPubSize = batchPubSize;
    }

    public boolean isEnabledPubsub() {
        return enabledPubsub;
    }

    public void setEnabledPubsub(boolean enabledPubsub) {
        this.enabledPubsub = enabledPubsub;
    }

    public CacheReporterConfig getCacheReporter() {
        return cacheReporter;
    }

    public void setCacheReporter(CacheReporterConfig cacheReporter) {
        this.cacheReporter = cacheReporter;
    }
}
