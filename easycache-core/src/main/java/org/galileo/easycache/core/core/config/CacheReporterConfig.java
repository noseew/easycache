package org.galileo.easycache.core.core.config;

import java.time.Duration;

public class CacheReporterConfig {

    /**
     * 导出开关
     */
    private boolean enabled;
    /**
     * 导出地址
     */
    private String remoteAddr;
    /**
     * 导出位置
     */
    private String reportPosition;
    /**
     * 窗口大小
     */
    private Duration timeWindow;
    /**
     * 窗口阈值
     */
    private int windowThreshold;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getRemoteAddr() {
        return remoteAddr;
    }

    public void setRemoteAddr(String remoteAddr) {
        this.remoteAddr = remoteAddr;
    }

    public String getReportPosition() {
        return reportPosition;
    }

    public void setReportPosition(String reportPosition) {
        this.reportPosition = reportPosition;
    }

    public Duration getTimeWindow() {
        return timeWindow;
    }

    public void setTimeWindow(Duration timeWindow) {
        this.timeWindow = timeWindow;
    }

    public int getWindowThreshold() {
        return windowThreshold;
    }

    public void setWindowThreshold(int windowThreshold) {
        this.windowThreshold = windowThreshold;
    }
}
