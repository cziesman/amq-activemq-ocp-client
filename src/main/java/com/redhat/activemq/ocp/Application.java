package com.redhat.activemq.ocp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jms.annotation.EnableJms;

@SpringBootApplication
@EnableJms
public class Application {

    public static void main(String[] args) {

        SpringApplication.run(Application.class, args);
    }

}
