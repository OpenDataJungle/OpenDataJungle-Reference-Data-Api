package com.opendatajungle.reference.data.api.client.security;

public final class SecurityExpressions {
    public static final String REFERENCEDATA_READ = "hasAuthority(@securityScopesProperties.referencedata.read)";
    public static final String REFERENCEDATA_WRITE = "hasAuthority(@securityScopesProperties.referencedata.write)";
    public static final String REFERENCEDATA_DELETE = "hasAuthority(@securityScopesProperties.referencedata.delete)";

    private SecurityExpressions() {
    }
}
