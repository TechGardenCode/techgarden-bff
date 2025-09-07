package gg.techgarden.bff.config;

import org.springframework.session.web.context.AbstractHttpSessionApplicationInitializer;

public class SessionInitializerConfig extends AbstractHttpSessionApplicationInitializer {

    public SessionInitializerConfig() {
        super(SessionConfig.class);
    }
}
