package apiGateway.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {
	
	@Value("${users.service.url:http://localhost:8770}")
    private String usersServiceUrl;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public WebClient webClient() {
        return WebClient.builder().build();
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
    public ReactiveAuthenticationManager authenticationManager(WebClient webClient) {
        return authentication -> {
            String email = authentication.getName();
            String password = authentication.getCredentials().toString();

            return webClient.get()
            		.uri(usersServiceUrl + "/users/email?email=" + email)                    .retrieve()
                    .onStatus(status -> status.is4xxClientError(),
                        response -> Mono.error(new RuntimeException("User not found")))
                    .bodyToMono(Map.class)
                    .flatMap(user -> {
                        String storedPassword = (String) user.get("password");
                        String role = (String) user.get("role");

                        if (password.equals(storedPassword)) {
                            return Mono.just(new UsernamePasswordAuthenticationToken(
                                    email, password,
                                    List.of(new SimpleGrantedAuthority("ROLE_" + role))));
                        } else {
                            return Mono.error(new RuntimeException("Invalid credentials"));
                        }
                    });
        };
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http,
            ReactiveAuthenticationManager authenticationManager) {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable())
            .authenticationManager(authenticationManager)
            .authorizeExchange(exchanges -> exchanges
                .pathMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .pathMatchers(HttpMethod.GET, "/users/login/**").permitAll()
                .pathMatchers("/currency-exchange/**").permitAll()
                .pathMatchers("/crypto-exchange/**").permitAll()
                .pathMatchers(HttpMethod.GET, "/currency-conversion/**").hasRole("USER")
                .pathMatchers(HttpMethod.GET, "/trade-service/**").hasRole("USER")
                .pathMatchers(HttpMethod.GET, "/bank-account").hasRole("ADMIN")
                .pathMatchers(HttpMethod.GET, "/bank-account/**").hasAnyRole("ADMIN", "USER")
                .pathMatchers(HttpMethod.POST, "/bank-account/**").hasRole("ADMIN")
                .pathMatchers(HttpMethod.PUT, "/bank-account/**").hasRole("ADMIN")
                .pathMatchers(HttpMethod.DELETE, "/bank-account/**").hasRole("ADMIN")
                .pathMatchers(HttpMethod.GET, "/crypto-wallet").hasRole("ADMIN")
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