package com.opendatajungle.reference.data.api;

import com.opendatajungle.commons.infra.conf.security.SecurityConfiguration;
import com.opendatajungle.commons.infra.conf.security.SecurityExceptionHandler;
import com.opendatajungle.commons.infra.conf.security.WithoutSecurityConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Import;

@SpringBootApplication(scanBasePackages = {
        "com.opendatajungle.reference.data.api",
        "com.opendatajungle.commons.client.config",
        "com.opendatajungle.commons.infra.conf.mdc",
        "com.opendatajungle.commons.infra.properties"
})
@Import({SecurityConfiguration.class, SecurityExceptionHandler.class, WithoutSecurityConfiguration.class})
@ConfigurationPropertiesScan
public class ReferenceDataApiApplication {

    static void main(String[] args) {
        SpringApplication.run(ReferenceDataApiApplication.class, args);
    }

}
