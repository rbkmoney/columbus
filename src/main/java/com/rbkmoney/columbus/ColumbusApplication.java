package com.rbkmoney.columbus;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@ServletComponentScan
@SpringBootApplication(scanBasePackages = {"com.rbkmoney.columbus"})
public class ColumbusApplication {

    public static void main(String[] args) {
        SpringApplication.run(ColumbusApplication.class, args);
    }
}

