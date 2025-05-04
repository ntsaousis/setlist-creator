package gr.jujuras.setlistplaylist.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/setlists/**").permitAll()  // 👈 allow public access
                        .anyRequest().authenticated()
                )
                .csrf(csrf -> csrf.disable()) // ✅ modern way to disable CSRF
                .httpBasic(Customizer.withDefaults())
                .build();
    }
}
