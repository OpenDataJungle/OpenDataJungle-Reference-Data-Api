package com.laulem.vectopath.referential.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@ConfigurationPropertiesScan
@SpringBootApplication
public class VectoPathReferentialApiApplication {

    static void main(String[] args) {
        SpringApplication.run(VectoPathReferentialApiApplication.class, args);
    }

}
