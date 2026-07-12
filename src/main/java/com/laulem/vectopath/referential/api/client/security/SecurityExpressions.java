package com.laulem.vectopath.referential.api.client.security;

public final class SecurityExpressions {
    public static final String REFERENTIAL_READ = "hasAuthority(@securityScopesProperties.referential.read)";
    public static final String REFERENTIAL_WRITE = "hasAuthority(@securityScopesProperties.referential.write)";
    public static final String REFERENTIAL_DELETE = "hasAuthority(@securityScopesProperties.referential.delete)";
    public static final String REFERENTIAL_ADMIN = "hasAuthority(@securityScopesProperties.referential.admin)";

    private SecurityExpressions() {
    }
}
