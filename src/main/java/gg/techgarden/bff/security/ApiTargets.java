package gg.techgarden.bff.security;

import java.util.Map;

public class ApiTargets {
    public static final String PROFILE_AUD = "api.profile";
    public static final String BLOG_AUD    = "api.blog";

    /** Map endpoint base → policy. Adjust for your routing. */
    public static final Map<String, CredentialPolicy> BY_BASE = Map.of(
            "profile", CredentialPolicy.USER_EXCHANGE,
            "blog", CredentialPolicy.USER_EXCHANGE
    );
}

