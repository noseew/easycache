package org.easycachetest.funtest.anno;

import org.easycachetest.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.TimeUnit;

@SpringBootTest
public class CircuitBreakerTest {

    @Autowired
    private UserService bookService;

    @Test
    public void test() throws InterruptedException {
        for (int i = 0; i < 10; i++) {
            bookService.get(1);
            TimeUnit.SECONDS.sleep(1);
        }
        bookService.get(1);
        System.out.println();
    }

}
