package org.galileo.easycache.core.exception;

/**
 * 缓存异常, 表示缓存操作流程中断了
 */
public class CacheInterruptException extends RuntimeException {

    private final int code;

    private final String msg;

    public CacheInterruptException(int code, String msg) {
        super(msg);
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
