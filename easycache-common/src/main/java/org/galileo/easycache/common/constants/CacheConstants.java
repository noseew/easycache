package org.galileo.easycache.common.constants;

import org.galileo.easycache.common.enums.ReportPositionType;

/**
 *
 */
public interface CacheConstants {
    String DEFAULT = "dft";
    String DEFAULT_NAMESPACE = DEFAULT;
    String DEFAULT_CACHE_NAME = DEFAULT;
    String DEFAULT_KEY = DEFAULT;
    boolean DEFAULT_ENABLED = false;
    int DEFAULT_EXPIRE = 1000 * 60 * 60 * 3;
    int NULL_VALUE_EXPIRE = 1000 * 10;
    int COMPRESS_THRESHOLD = 10 * 1024;
    long WAIT_MILL = 5 * 1000L;
    long RENEWAL_TIME = 1000 * 60L;
    boolean DEFAULT_CACHE_NULL_VALUE = false;

    String CACHE_CONFIG = "easyCacheConfig";
    String CACHE_BEAN_NAME = "easyCache";

    boolean DEFAULT_LOCAL_ENABLED = false;
    int DEFAULT_LOCAL_EXPIRE_FALLBACK = 1000 * 60 * 3;
    int DEFAULT_LOCAL_EXPIRE = 1000 * 5;
    int DEFAULT_LOCAL_LIMIT = 1000;

    boolean BIGKEY_ENABLESIZELIMIT = true;
    boolean FORBIDDEN_EXCEPTION = false;
    int BIGKEY_WARNSIZE = 5 * 1024;
    int BIGKEY_WARNLEN = 500;
    int BIGKEY_FORBIDDENSIZE = 20 * 1024;
    int BIGKEY_FORBIDDENLEN = 5000;

    boolean REPORTER_ENABLE = false;
    String REPORTER_POSITION = ReportPositionType.LOG.getVal();


    int FIVE_MIN = 1000 * 60 * 5;
    int ONE_HOUR = 1000 * 60 * 60;
    int ONE_DAY = 1000 * 60 * 60 * 24;

    int CODE_OK = 0;
    int CODE_ERROR_NET = 10;
    int CODE_ERROR_CODEC = 20;
    int CODE_ERROR_CAST = 25;
    int CODE_ERROR_FILTER = 30;
    int CODE_ERROR_PARAM = 80;
    int CODE_ERROR_OTHER = 99;

    static String cacheBeanName(String ns) {
        return CACHE_BEAN_NAME + "_" + ns;
    }
}
