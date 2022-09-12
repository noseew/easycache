package org.galileo.easycache.common;

import java.util.function.Function;

/**
 * 缓存序列化策略接口
 */
public interface SerialPolicy {

    String STRING = "String";

    String Jackson = "Jackson";
    
    String Gzip = "Gzip";
    
    String Protostuff = "Protostuff";

    String name();

    /**
     * 解码(反序列化)
     * 
     * @return
     */
    Function<Object, byte[]> encoder();

    /**
     * 编码(序列化)
     * 
     * @return
     */
    Function<byte[], Object> decoder();
}
