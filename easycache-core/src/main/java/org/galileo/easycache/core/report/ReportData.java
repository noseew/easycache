package org.galileo.easycache.core.report;

public class ReportData {
    private String host;
    private String ip;
    private String appName;
    private long ts;
    /**
     * 数据类型, 0-监控统计信息, 1-管理信息
     */
    private int type;
}
