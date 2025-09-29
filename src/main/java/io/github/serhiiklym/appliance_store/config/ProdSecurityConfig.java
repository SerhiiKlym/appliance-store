package io.github.serhiiklym.appliance_store.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@Profile("prod")
public class ProdSecurityConfig {

    private final Environment env;

    public ProdSecurityConfig(Environment env) {
        this.env = env;
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }
    @Bean
    UserDetailsService userDetailsService(PasswordEncoder encoder) {
        String adminPwd    = requireEnv("APP_ADMIN_PASSWORD");
        String employeePwd = requireEnv("APP_EMPLOYEE_PASSWORD");
        String clientPwd   = requireEnv("APP_CLIENT_PASSWORD");

        UserDetails admin = User.withUsername("admin")
                .password(encoder.encode(adminPwd))
                .roles("ADMIN")
                .build();

        UserDetails employee = User.withUsername("employee")
                .password(encoder.encode(employeePwd))
                .roles("EMPLOYEE")
                .build();

        UserDetails client = User.withUsername("client")
                .password(encoder.encode(clientPwd))
                .roles("CLIENT")
                .build();

        return new InMemoryUserDetailsManager(admin, employee, client);
    }

    private String requireEnv(String name) {
        String value = env.getProperty(name); // Spring exposes OS env vars here
        if (value == null || value.isBlank()) {
            throw new IllegalStateException("Missing required environment variable: " + name);
        }
        return value;
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // CSRF is ON by default
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/css/**", "/js/**", "/img/**", "/login").permitAll()
                        .requestMatchers("/h2-console/**").denyAll() // block console in prod
                        .requestMatchers("/manufacturers/**", "/appliances/**", "/clients/**", "/employees/**").hasAnyRole("ADMIN","EMPLOYEE")
                        .anyRequest().authenticated()
                )
                .formLogin(login -> login.loginPage("/login").permitAll())
                .logout(logout -> logout.logoutSuccessUrl("/"));
        return http.build();
    }
}
