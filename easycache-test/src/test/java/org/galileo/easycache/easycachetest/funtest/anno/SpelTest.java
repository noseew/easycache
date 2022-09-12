package org.galileo.easycache.easycachetest.funtest.anno;

import org.galileo.easycache.easycachetest.entity.UserDO;
import org.galileo.easycache.easycachetest.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class SpelTest {

    @Autowired
    private UserService bookService;

    @Test
    public void test() {

        UserDO bookDO = new UserDO();
        bookDO.setId(1);
        bookService.getSpel1(1);
        bookService.getSpel2(bookDO);
        bookService.getSpel3("org", bookDO);

        System.out.println();
    }
}
