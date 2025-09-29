package io.github.serhiiklym.appliance_store.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@SpringBootTest
@AutoConfigureMockMvc
public class SecuritySmokeTest {

    @Autowired
    MockMvc mvc;

    @Test
    @DisplayName("Anonymous users should be redirected to /login when hitting a protected URL")
    void anonymous_is_redirected_to_login_on_protected_page() throws Exception {
        mvc.perform(get("/manufacturers"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    @WithMockUser(username = "emp", roles = {"EMPLOYEE"})
    @DisplayName("Employee should be allowed to view manufacturers")
    void employee_can_access_manufacturers() throws Exception {
        mvc.perform(get("/manufacturers"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "cli", roles = {"CLIENT"})
    @DisplayName("Client is forbidden from Manufacturers endpoint")
    void client_is_forbidden_from_manufacturers() throws Exception {
        mvc.perform(get("/manufacturers"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "ad", roles = {"ADMIN"})
    @DisplayName("No user is allowed to H2 console on prod env")
    void users_forbidden_from_h2_console_on_prod() throws Exception {
        mvc.perform(get("/h2-console"))
                .andExpect(status().is(404));
    }


}
