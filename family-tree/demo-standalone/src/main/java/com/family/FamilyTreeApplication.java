package com.family;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.family.mapper")
public class FamilyTreeApplication {
    public static void main(String[] args) {
        SpringApplication.run(FamilyTreeApplication.class, args);
    }
}
