package org.easycachetest.funtest.base;

import org.galileo.easycache.common.ValWrapper;
import org.galileo.easycache.core.utils.InnerCodecUtilsDemo;
import org.junit.jupiter.api.Test;

public class CodecUtils {

    @Test
    public void test01() {
        String val = "[\"org.song.cache.easycache.common.ValWrapper\",{\"expire\":10800000,\"expireTs\":1657867038014,\"realExpireTs\":1657867038014,\"valueSerialName\":\"Jackson\",\"valueCompSerialName\":null,\"size\":147,\"value\":[\"[B\",\"WyJjb20ueXh0LnVjYWNoZS51Y2FjaGV0ZXN0LmVudGl0eS5Vc2VyRE8iLHsiaWQiOjAsImFnZSI6MzAsIm5hbWUiOiIzZDA3ZSIsInNhbGFyeSI6WyJqYXZhLm1hdGguQmlnRGVjaW1hbCIsMTM5LjgyNjU0MDU0MDEyMzc3XSwiY3JlYXRlVGltZSI6bnVsbH1d\"]}]";
        try {
            ValWrapper valWrapper = InnerCodecUtilsDemo.deSerialVal(val);
            System.out.println(valWrapper);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
