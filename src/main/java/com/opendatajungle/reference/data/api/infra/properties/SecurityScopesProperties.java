package com.opendatajungle.reference.data.api.infra.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "security.scopes")
public class SecurityScopesProperties {
    private ReferenceData referencedata;

    @Getter
    @Setter
    public static class ReferenceData {
        private String read;
        private String write;
        private String delete;
        private String admin;
    }
}
