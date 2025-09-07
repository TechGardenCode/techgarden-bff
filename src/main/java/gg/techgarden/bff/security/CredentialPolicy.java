package gg.techgarden.bff.security;

public enum CredentialPolicy {
    /** Get user token from session, exchange to target audience with requested scopes. */
    USER_EXCHANGE,

    /** Use client-credentials (BFF’s own) for pure m2m calls from the BFF (optional here). */
    CLIENT_CREDENTIALS,

    /** Relay (NOT recommended across service boundary unless audience matches) */
    USER_RELAY
}
