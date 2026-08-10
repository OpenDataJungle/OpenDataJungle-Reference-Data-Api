package com.opendatajungle.reference.data.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@ConfigurationPropertiesScan
@SpringBootApplication
public class ReferenceDataApiApplication {

    static void main(String[] args) {
        SpringApplication.run(ReferenceDataApiApplication.class, args);
    }

}
