package io.github.serhiiklym.appliance_store.security;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@Disabled("Enable when create/delete endpoints are ready")
class CsrfEnforcementTest {

    @Test
    void post_without_csrf_is_rejected() {
        // TODO: WithMockUser(roles="EMPLOYEE")
        // POST /manufacturers (no csrf()) → expect 403
    }

    @Test
    void post_with_csrf_is_accepted() {
        // TODO: WithMockUser(roles="EMPLOYEE")
        // POST /manufacturers with .with(csrf()) → expect 3xx to success page
    }
}
