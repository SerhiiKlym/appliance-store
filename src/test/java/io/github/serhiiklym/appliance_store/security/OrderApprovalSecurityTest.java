package io.github.serhiiklym.appliance_store.security;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@Disabled("Implement order approval first, then enable these tests")
class OrderApprovalSecurityTest {

    @Test
    void anonymous_cannot_approve_order() {
        // TODO: perform POST /orders/{id}/approve without auth → expect 302 to login
    }

    @Test
    void client_cannot_approve_order() {
        // TODO: WithMockUser(roles="CLIENT") → expect 403
    }

    @Test
    void employee_can_approve_order() {
        // TODO: WithMockUser(roles="EMPLOYEE") POST with csrf → expect 302/200 depending on flow
    }
}
