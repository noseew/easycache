package org.easycachetest.funtest.base;

import com.google.common.base.Objects;
import lombok.Data;
import org.assertj.core.util.Sets;
import org.galileo.easycache.common.ValWrapper;
import org.galileo.easycache.core.core.JacksonSerializer;
import org.galileo.easycache.core.utils.BatchUtils;
import org.galileo.easycache.core.utils.InnerProtostuffUtils;
import org.galileo.easycache.core.utils.InnerSizeUtils;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;

public class UtilsTest {

    @Test
    public void deserial() {
        String val = "WyJjb20ueXh0LmFjLmVudGl0eS5BY3Rpdml0eSIseyJpZCI6MTQ5ODUzOTI5NTU2MTE1ODY1OCwib3JnSWQiOiJhYzBhMjY3Yy0wNDRlLTRhMjgtYjQ4OS02YmUxNmUwYWI3ODgiLCJlbnJvbGxJZCI6bnVsbCwidGl0bGUiOiLmtYvor5XmtLvliqjkuK3lv4PmiqXlkI0iLCJ3b3Jrc0F1ZGl0IjowLCJhY1N0YXR1cyI6MCwibXVsdGlzdGFnZSI6MCwidXNhYmxlIjowLCJhY3Rpdml0eUxpbmsiOm51bGwsImRlbGV0ZWQiOjAsImNyZWF0ZVVzZXJJZCI6IjAwZjU3NjBkLWY5MjEtNDI0Zi1hZGFhLTZhOTRiODEyYmFhYiIsImNyZWF0ZVRpbWUiOm51bGwsInVwZGF0ZVVzZXJJZCI6IjAwZjU3NjBkLWY5MjEtNDI0Zi1hZGFhLTZhOTRiODEyYmFhYiIsInVwZGF0ZVRpbWUiOiIyMDIyLTAzLTI4IDEzOjUzOjE1Ljc5MCIsInB1Ymxpc2hUaW1lIjpudWxsfV0=";
//        JacksonSerializer jacksonSerializer = new JacksonSerializer();
//        Object apply = jacksonSerializer.decoder().apply(val.getBytes(StandardCharsets.UTF_8));
//        System.out.println(apply);

        byte[] decode = Base64.getDecoder().decode(val.getBytes(StandardCharsets.UTF_8));
        String s = new String(decode, StandardCharsets.UTF_8);
        System.out.println();
    }

    @Test
    public void codec() {
        JacksonSerializer jacksonSerializer = new JacksonSerializer();

        User user = new User();
        user.setName("user");
        user.setUserId(11);
        Object userSerl = jacksonSerializer.encoder().apply(user);
        ValWrapper valWrapper = ValWrapper.createInstance(1000, userSerl);
        byte[] bytes = jacksonSerializer.encoder().apply(valWrapper);

        ValWrapper valWrapper2 = (ValWrapper) jacksonSerializer.decoder().apply(bytes);
        Object user2 = jacksonSerializer.decoder().apply((byte[]) valWrapper2.getValue());
        valWrapper2.setValue(user2);

        System.out.println();
    }

    @Test
    public void stringTest01() {
        int a = 1, b = 2, c = 3, d = 4, e = 5;
        long start = System.currentTimeMillis();
        for (int i = 0; i < 10_0000; i++) {
            String ss = "" + a + b + c + d + e; // 17
//            StringUtils.append(a, b, c, d, e); // 45
//            String.format("%s%s%s%s%s", a, b, c, d, e); // 300
        }
        long end = System.currentTimeMillis();
        System.out.println(end - start);
    }

//    @Test
//    public void testPerf() {
//        String key = "key";
//        System.out.println(RandomUtil.random(key));
//        long start = System.currentTimeMillis();
//        for (int i = 0; i < 1000_0000; i++) {
////            UUID.randomUUID().toString();
//            RandomUtil.random(key); // 比UUID快约7倍
//        }
//        long end = System.currentTimeMillis();
//        System.out.println(end - start);
//    }
//
//    @Test
//    public void testRepeat() {
//        String key = "key";
//        long start = System.currentTimeMillis();
//        Set<String> set = new CopyOnWriteArraySet<>();
//        for (int i = 0; i < 20_0000; i++) {
//            try {
//                ThreadUtils.poolExecutor.execute(() -> {
//                    String random = RandomUtil.random(key);
//                    if (!set.add(random)) {
//                        System.out.println(random);
//                    }
//                });
//            } catch (Exception e) {
//                try {
//                    TimeUnit.MILLISECONDS.sleep(1);
//                } catch (InterruptedException interruptedException) {
//                }
//            }
//        }
//        if (ThreadUtils.poolExecutor.getActiveCount() > 0) {
//            try {
//                TimeUnit.MILLISECONDS.sleep(1000 * 1);
//            } catch (InterruptedException interruptedException) {
//            }
//        }
//        ThreadUtils.poolExecutor.shutdown();
//        long end = System.currentTimeMillis();
//        System.out.println(end - start);
//    }

    @Test
    public void sizeTest() {
        assert InnerSizeUtils.parseSize("12") == 12;
        assert InnerSizeUtils.parseSize(" 12 ") == 12;
        assert InnerSizeUtils.parseSize("20b") == 20;
        assert InnerSizeUtils.parseSize("20B") == 20;
        assert InnerSizeUtils.parseSize(" 20B ") == 20;
        assert InnerSizeUtils.parseSize("20kB") == 20 * 1024;
        assert InnerSizeUtils.parseSize(" 20kB ") == 20 * 1024;
        assert InnerSizeUtils.parseSize("20k") == 20 * 1024;
        assert InnerSizeUtils.parseSize("20k ") == 20 * 1024;
        assert InnerSizeUtils.parseSize("20m") == 20 * 1024 * 1024;
        assert InnerSizeUtils.parseSize("20M") == 20 * 1024 * 1024;
        assert InnerSizeUtils.parseSize("20Mb") == 20 * 1024 * 1024;
        assert InnerSizeUtils.parseSize("20gb") == 20 * 1024 * 1024 * 1024;
    }

    @Test
    public void protostuffTest() {
        User user = new User();
        byte[] serialize = InnerProtostuffUtils.serialize(user);
        User user1 = InnerProtostuffUtils.deserialize(serialize, User.class);

        assert user.equals(user1);
    }

    @Test
    public void batchTest() {
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < 1250; i++) {
            list.add(i);
        }

        assert BatchUtils.batchSet(Sets.newHashSet(list), e -> e != null);
        BatchUtils.batchSet(Sets.newHashSet(list), e -> {});

        assert BatchUtils.batchList(list, e -> e != null);
        BatchUtils.batchList(list, e -> {});

    }

    @Data
    static class User {
        private int userId;
        private String name;
        private Date birthday = new Date();
        private LocalDateTime localDateTime = LocalDateTime.now();
        private BigDecimal salary = BigDecimal.valueOf(0.999);
        private List<String> tags = new ArrayList<>();

        @Override
        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (!(o instanceof User))
                return false;
            User user = (User) o;
            return userId == user.userId && Objects.equal(name, user.name) && Objects.equal(birthday, user.birthday)
                    && Objects.equal(localDateTime, user.localDateTime) && Objects.equal(salary, user.salary) && Objects
                    .equal(tags, user.tags);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(userId, name, birthday, localDateTime, salary, tags);
        }
    }
}
