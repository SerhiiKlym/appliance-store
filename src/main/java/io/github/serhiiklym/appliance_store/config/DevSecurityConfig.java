package io.github.serhiiklym.appliance_store.config;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
@Profile("dev")
public class DevSecurityConfig {

    @Bean
    SecurityFilterChain devSecurity(HttpSecurity http) throws Exception {

        // Allow H2 console + static assets without auth; everything else requires auth
        http.authorizeHttpRequests(auth -> auth
                        .requestMatchers(PathRequest.toH2Console()).permitAll()
                        .requestMatchers("/", "/error", "/css/**", "/js/**", "/images/**").permitAll()
                        .anyRequest().authenticated()
                )

                // Ignore CSRF only for the H2 console's POSTs (e.g., /h2-console/login.do)
                .csrf(csrf -> csrf.ignoringRequestMatchers(PathRequest.toH2Console()))

                // Permit same-origin frames so the H2 UI (frameset) can render
                .headers(h -> h.frameOptions(f -> f.sameOrigin()))

                // Keep simple login mechanisms available in dev
                .formLogin(Customizer.withDefaults())
                .httpBasic(Customizer.withDefaults());

        return http.build();
    }
}
