package de.goldmann.vectordatabases.api.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;


@Configuration
@EnableWebSecurity
@Slf4j
public class WebSecurityConfig {

    @Bean
    RestTemplate restTemplate(){
        return new RestTemplate();
    }

    @Bean
    public SecurityFilterChain defaultSecurityFilterChain(final HttpSecurity http) throws Exception {
        http.securityMatcher(new AntPathRequestMatcher("/**"));

        this.configureAuthorization(http);
        this.configureCors(http);

        // Disable CSRF
        http.csrf().disable();

        return http.build();
    }

    void configureAuthorization(final HttpSecurity httpSecurity) throws Exception {
        // Permit all to allow anonymous access just for test
        httpSecurity.authorizeHttpRequests()
                .anyRequest()
                .permitAll();

    }

    // CORS (Cross-Origin Resource Sharing)
    void configureCors(final HttpSecurity httpSecurity) throws Exception {
        httpSecurity.cors()
                .configurationSource(this.corsConfigurationSource());
    }

    public CorsConfigurationSource corsConfigurationSource() {
        final CorsConfiguration configuration = new CorsConfiguration();
        configuration.applyPermitDefaultValues();
        //configuration.setAllowCredentials(true);
        configuration.setAllowedOrigins(List.of("*"));

        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

}