package org.easycachetest.comp;

import com.google.common.collect.Lists;
import org.easycachetest.entity.UserDO;
import org.easycachetest.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class TestCommon {

    @Autowired
    protected UserService userService;

    protected Random random = new Random();

    public Runnable lb(int[] wight) {
        int total = 0;
        for (int i : wight) {
            total += i;
        }
        int i = random.nextInt(total);

        total = 0;
        List<Runnable> runnables = runnableList();
        for (int j = 0; j < wight.length; j++) {
            total += wight[j];
            if (total >= i) {
                return runnables.get(j);
            }
        }
        return runnables.get(0);
    }

    public List<Runnable> runnableList() {
        return Lists.newArrayList(add1(), del1(), update1(), cached1());
    }

    public int[] runnableWight() {
        return new int[]{5, 5, 20, 80};
    }

    public Runnable add1() {
        return () -> {
            try {
                UserDO userDO = new UserDO();
                userDO.setName(UUID.randomUUID().toString().substring(0, 5));
                userDO.setId((int) (Math.random() * 1000));
                userDO.setSalary(BigDecimal.valueOf(Math.random() * 1000));
                userDO.setAge((int) (Math.random() * 100));
                userDO.setCreateTime(new Date());
                userService.add(userDO);
            } catch (Exception e) {
                if (e instanceof DuplicateKeyException) {
                    return;
                }
                System.out.println("业务方法抛出异常");
                e.printStackTrace();
            }
        };
    }
    public Runnable add1(int id) {
        return () -> {
            try {
                UserDO userDO = new UserDO();
                userDO.setName(UUID.randomUUID().toString().substring(0, 5));
                userDO.setId(id);
                userDO.setSalary(BigDecimal.valueOf(Math.random() * 1000));
                userDO.setAge((int) (Math.random() * 100));
                userDO.setCreateTime(new Date());
                userService.add(userDO);
            } catch (Exception e) {
                if (e instanceof DuplicateKeyException) {
                    return;
                }
                System.out.println("业务方法抛出异常");
                e.printStackTrace();
            }
        };
    }

    public Runnable cached1() {
        return () -> {
            int key = (int) (Math.random() * 1000);
            try {
                userService.get(key);
            } catch (Exception e) {
                System.out.println("业务方法抛出异常");
                e.printStackTrace();
                System.out.println(key);
            }
        };
    }

    public Runnable update1() {
        return () -> {
            try {
                UserDO userDO = new UserDO();
                userDO.setName(UUID.randomUUID().toString().substring(0, 5));
                userDO.setId((int) (Math.random() * 1000));
                userDO.setSalary(BigDecimal.valueOf(Math.random() * 1000));
                userDO.setAge((int) (Math.random() * 100));
                userService.update(userDO);
            } catch (Exception e) {
                System.out.println("业务方法抛出异常");
                e.printStackTrace();
            }
        };
    }

    public Runnable del1() {
        return () -> {
            try {
                userService.del((int) (Math.random() * 1000));
            } catch (Exception e) {
                System.out.println("业务方法抛出异常");
                e.printStackTrace();
            }
        };
    }

}
