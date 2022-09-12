package org.galileo.easycache.core.utils;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DecimalFormat;

public class InnerSizeUtils {

    private static Logger logger = LoggerFactory.getLogger(InnerSizeUtils.class);

    private static final DecimalFormat df = new DecimalFormat("#.00");

    public static String sizeFormat(int size) {
        return BitSize.sizeFormat(size);
    }

    public static int parseSize(String val) {
        if (StringUtils.isEmpty(val)) {
            return 0;
        }
        try {
            return BitSize.parseByteSize(val);
        } catch (Exception e) {
            // ignore
            logger.warn("", e);
        }
        return 0;
    }

    enum BitSize {
        GB("gb", "g", 1024 * 1024 * 1024),
        MB("mb", "m", 1024 * 1024),
        KB("kb", "k", 1024),
        B("b", "", 1),
        ;
        private String unit1;
        private String unit2;
        private int muilti;

        public static int parseByteSize(String val) {
            if (StringUtils.isEmpty(val)) {
                return 0;
            }
            if (NumberUtils.isCreatable(val = val.trim())) {
                return NumberUtils.toInt(val);
            }

            BitSize bitSize = null;
            val = val.toLowerCase();
            int last = val.length() - 1;
            for (BitSize value : values()) {
                if (val.endsWith(value.unit1)) {
                    last = val.length() - value.unit1.length();
                    bitSize = value;
                    break;
                }
                if (val.endsWith(value.unit2)) {
                    last = val.length() - value.unit2.length();
                    bitSize = value;
                    break;
                }
            }
            if (bitSize == null) {
                throw new IllegalArgumentException("参数有误: " + val);
            }
            return NumberUtils.toInt(String.valueOf(val.toCharArray(), 0, last)) * bitSize.muilti;
        }

        public static String sizeFormat(int size){
            if (size <= 0) {
                return "0";
            } else if (size < KB.muilti) {
                return df.format((double) size / B.muilti) + B.unit1;
            } else if (size < MB.muilti) {
                return df.format((double) size / KB.muilti) + KB.unit1;
            } else if (size < GB.muilti) {
                return df.format((double) size / MB.muilti) + MB.unit1;
            }
            return df.format((double) size / GB.muilti) + GB.unit1;
        }

        BitSize(String unit1, String unit2, int muilti) {
            this.unit1 = unit1;
            this.unit2 = unit2;
            this.muilti = muilti;
        }

        public String getUnit1() {
            return unit1;
        }

        public String getUnit2() {
            return unit2;
        }

        public int getMuilti() {
            return muilti;
        }
    }
}

