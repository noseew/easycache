package org.galileo.easycache.common.constants;

public class SubNamespace {
    private SubNamespace() {

    }
    public static final String LOCAL_POSTFIX = "_local";
    public static final String REMOTE_POSTFIX = "_remote";
    public static final String DFT_LOCAL = CacheConstants.DEFAULT_NAMESPACE + LOCAL_POSTFIX;
    public static final String DFT_REMOTE = CacheConstants.DEFAULT_NAMESPACE + REMOTE_POSTFIX;
}
