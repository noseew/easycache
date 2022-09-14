package org.easycachetest.funtest.anno;

import org.easycachetest.entity.UserDO;
import org.easycachetest.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class OgnlTest {

    @Autowired
    private UserService bookService;

    @Test
    public void test() {

        UserDO bookDO = new UserDO();
        bookDO.setId(1);
        bookService.getOgel1(1);
        bookService.getOgel2(bookDO);
        bookService.getOgel3("org", bookDO);

        System.out.println();
    }

}
