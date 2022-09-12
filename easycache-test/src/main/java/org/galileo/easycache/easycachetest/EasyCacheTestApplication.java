package org.galileo.easycache.easycachetest;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("org.galileo.easycache")
@MapperScan("org.galileo.easycache.easycachetest")
public class EasyCacheTestApplication {

    public static void main(String[] args) {
        SpringApplication.run(EasyCacheTestApplication.class, args);
    }

}
