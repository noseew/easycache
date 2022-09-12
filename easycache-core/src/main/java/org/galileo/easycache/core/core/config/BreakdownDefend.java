package org.galileo.easycache.core.core.config;


import org.galileo.easycache.common.enums.BreakdownType;

import java.time.Duration;

public class BreakdownDefend {
    /**
     * 击穿防护类型
     */
    private String type = BreakdownType.NONE.getVal();
    /**
     * 续期时间
     */
    private Duration renewalTime = Duration.ofSeconds(60);

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Duration getRenewalTime() {
        return renewalTime;
    }

    public void setRenewalTime(Duration renewalTime) {
        this.renewalTime = renewalTime;
    }
}
