package apiGateway.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:4200"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public MapReactiveUserDetailsService userDetailsService(PasswordEncoder encoder) {
        UserDetails owner = User.builder()
                .username("owner@soas.com")
                .password(encoder.encode("owner123"))
                .roles("OWNER")
                .build();

        UserDetails admin = User.builder()
                .username("admin@soas.com")
                .password(encoder.encode("admin123"))
                .roles("ADMIN")
                .build();

        UserDetails user = User.builder()
                .username("user@soas.com")
                .password(encoder.encode("user123"))
                .roles("USER")
                .build();

        return new MapReactiveUserDetailsService(owner, admin, user);
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable())
            .authorizeExchange(exchanges -> exchanges
                .pathMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .pathMatchers("/currency-exchange/**").permitAll()
                .pathMatchers("/crypto-exchange/**").permitAll()
                .pathMatchers(HttpMethod.GET, "/currency-conversion/**").hasRole("USER")
                .pathMatchers(HttpMethod.GET, "/trade-service/**").hasRole("USER")
                .pathMatchers(HttpMethod.GET, "/bank-account/**").hasAnyRole("ADMIN", "USER")
                .pathMatchers(HttpMethod.POST, "/bank-account/**").hasRole("ADMIN")
                .pathMatchers(HttpMethod.PUT, "/bank-account/**").hasRole("ADMIN")
                .pathMatchers(HttpMethod.DELETE, "/bank-account/**").hasRole("ADMIN")
                .pathMatchers(HttpMethod.GET, "/crypto-wallet/**").hasAnyRole("ADMIN", "USER")
                .pathMatchers(HttpMethod.POST, "/crypto-wallet/**").hasRole("ADMIN")
                .pathMatchers(HttpMethod.PUT, "/crypto-wallet/**").hasRole("ADMIN")
                .pathMatchers(HttpMethod.DELETE, "/crypto-wallet/**").hasRole("ADMIN")
                .pathMatchers(HttpMethod.GET, "/users/**").hasAnyRole("OWNER", "ADMIN")
                .pathMatchers(HttpMethod.POST, "/users/**").hasAnyRole("OWNER", "ADMIN")
                .pathMatchers(HttpMethod.PUT, "/users/**").hasAnyRole("OWNER", "ADMIN")
                .pathMatchers(HttpMethod.DELETE, "/users/**").hasRole("OWNER")
                .anyExchange().authenticated()
            )
            .httpBasic(org.springframework.security.config.Customizer.withDefaults());

        return http.build();
    }
}