package io.github.serhiiklym.appliance_store.security;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

@Disabled("Turn on when profiles are finalized")
@ActiveProfiles("dev")
class H2ConsoleSecurityDevTest {

    @Test
    void h2_console_is_accessible_in_dev() {
        // TODO: GET /h2-console/ → expect 200 or 3xx to /h2-console/login.do depending on config
    }
}

@Disabled("Turn on when profiles are finalized")
@ActiveProfiles("prod")
class H2ConsoleSecurityProdTest {

    @Test
    void h2_console_is_blocked_in_prod() {
        // TODO: GET /h2-console/ → expect 404 or 403
    }
}
