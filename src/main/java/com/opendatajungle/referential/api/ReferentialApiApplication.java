package com.opendatajungle.referential.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@ConfigurationPropertiesScan
@SpringBootApplication
public class ReferentialApiApplication {

    static void main(String[] args) {
        SpringApplication.run(ReferentialApiApplication.class, args);
    }

}
