package org.galileo.easycache.core.core.config;

import org.galileo.easycache.common.constants.CacheConstants;

import java.time.Duration;

public class CircuitBreakerConfig {

    /**
     *
     */
    private boolean enabled = true;
    /**
     *
     */
    private Duration expireAfterWrite = Duration.ofMillis(CacheConstants.DEFAULT_LOCAL_EXPIRE_FALLBACK);

    /**
     * 配置滑动窗口类型。当CircuitBreaker关闭时，这种类型的滑动窗口会记录调用结果。
     * 滑动窗口要么是基于计数的，要么是基于时间的。
     * 若滑动窗口为COUNT_BASED，则最近slidingWindowSize次的调用会被记录和统计。
     * 若滑动窗口为TIME_BASED，则最近slidingWindowSize秒中的调用会被记录和统计。
     */
    private String slidingWindowType = "COUNT_BASED";
    /**
     * 以百分率形式配置失败率阈值。失败率大于等于阈值时，CircuitBreaker转变为打开状态，并使调用短路。
     */
    private int failureRateThreshold = 50;
    /**
     * 以百分率形式配置慢调用率阈值。当调用执行的时长大于slowCallDurationThreshold时，CircuitBreaker会认为调用为慢调用。
     * 当慢调用占比大于等于此阈值时，CircuitBreaker转变为打开状态，并使调用短路。
     */
    private int slowCallRateThreshold = 100;
    /**
     * 配置调用执行的时长阈值。当超过这个阈值时，调用会被认为是慢调用，并增加慢调用率。
     */
    private Duration slowCallDurationThreshold = Duration.ofMillis(5000);
    /**
     * 配置最小调用次数。在CircuitBreaker计算错误率前，要求（在每滑动窗口周期）用到它。
     * 例如，若minimumNumberOfCalls是10，为计算失败率，则最小要记录10个调用。若只记录了9个调用，即使9个都失败，CircuitBreaker也不会打开。
     */
    private int minimumNumberOfCalls = 10;
    /**
     * 当CircuitBreaker是半开状态时，配置被允许的调用次数。
     */
    private int permittedNumberOfCallsInHalfOpenState = 10;
    /**
     * 配置滑动窗口的大小。当CircuitBreaker关闭后用于记录调用结果。
     */
    private int slidingWindowSize = 10;
    /**
     * CircuitBreaker状态从打开转化为半开时，需要等待的时长。
     */
    private Duration waitDurationInOpenState = Duration.ofMillis(6000);
    /**
     * 如果为true，则CircuitBreaker会自动从打开状态转化为半开状态。不需要另外的调用来触发这种转换。
     */
    private boolean automaticTransitionFromOpenToHalfOpenEnabled = false;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getSlidingWindowType() {
        return slidingWindowType;
    }

    public void setSlidingWindowType(String slidingWindowType) {
        this.slidingWindowType = slidingWindowType;
    }

    public int getFailureRateThreshold() {
        return failureRateThreshold;
    }

    public void setFailureRateThreshold(int failureRateThreshold) {
        this.failureRateThreshold = failureRateThreshold;
    }

    public int getSlowCallRateThreshold() {
        return slowCallRateThreshold;
    }

    public void setSlowCallRateThreshold(int slowCallRateThreshold) {
        this.slowCallRateThreshold = slowCallRateThreshold;
    }

    public Duration getSlowCallDurationThreshold() {
        return slowCallDurationThreshold;
    }

    public void setSlowCallDurationThreshold(Duration slowCallDurationThreshold) {
        this.slowCallDurationThreshold = slowCallDurationThreshold;
    }

    public int getMinimumNumberOfCalls() {
        return minimumNumberOfCalls;
    }

    public void setMinimumNumberOfCalls(int minimumNumberOfCalls) {
        this.minimumNumberOfCalls = minimumNumberOfCalls;
    }

    public int getPermittedNumberOfCallsInHalfOpenState() {
        return permittedNumberOfCallsInHalfOpenState;
    }

    public void setPermittedNumberOfCallsInHalfOpenState(int permittedNumberOfCallsInHalfOpenState) {
        this.permittedNumberOfCallsInHalfOpenState = permittedNumberOfCallsInHalfOpenState;
    }

    public int getSlidingWindowSize() {
        return slidingWindowSize;
    }

    public void setSlidingWindowSize(int slidingWindowSize) {
        this.slidingWindowSize = slidingWindowSize;
    }

    public Duration getWaitDurationInOpenState() {
        return waitDurationInOpenState;
    }

    public void setWaitDurationInOpenState(Duration waitDurationInOpenState) {
        this.waitDurationInOpenState = waitDurationInOpenState;
    }

    public boolean isAutomaticTransitionFromOpenToHalfOpenEnabled() {
        return automaticTransitionFromOpenToHalfOpenEnabled;
    }

    public void setAutomaticTransitionFromOpenToHalfOpenEnabled(boolean automaticTransitionFromOpenToHalfOpenEnabled) {
        this.automaticTransitionFromOpenToHalfOpenEnabled = automaticTransitionFromOpenToHalfOpenEnabled;
    }

    public Duration getExpireAfterWrite() {
        return expireAfterWrite;
    }

    public void setExpireAfterWrite(Duration expireAfterWrite) {
        this.expireAfterWrite = expireAfterWrite;
    }
}
