package org.easycachetest.funtest.anno;


import org.easycachetest.entity.UserDO;
import org.easycachetest.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@SpringBootTest
public class CombinationCacheAnnoTest {

    @Autowired
    private UserService userService;

    @Test
    public void cacheTest01() {
        UserDO userDO = new UserDO();
        userDO.setId(11);
        userDO.setName("name xxx");
        userDO.setCreateTime(new Date());
        userDO.setSalary(BigDecimal.valueOf(3000));
        userService.add(userDO);

    }

    @Test
    public void cacheTest02() {
        UserDO user = userService.get(11);
        System.out.println(user);

        List<UserDO> userList = userService.getList();
        System.out.println(userList);
    }

    @Test
    public void cacheTest03() {
        UserDO userDO = new UserDO();
        userDO.setId(11);
        userDO.setName("name xxx " + UUID.randomUUID().toString());
        userDO.setCreateTime(new Date());
        userDO.setSalary(BigDecimal.valueOf(4000));
        userService.update(userDO);

        UserDO user = userService.get(11);
        System.out.println(user);

    }

    @Test
    public void cacheTest04() {
        userService.del(11);
    }


}
