package apiGateway.routing;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RoutingConfiguration {

    @Bean
    RouteLocator setRoutes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("currency-exchange", p -> p.path("/currency-exchange/**").uri("lb://currency-exchange"))
                .route("currency-conversion", p -> p.path("/currency-conversion/**").uri("lb://currency-conversion"))
                .route("users-service", p -> p.path("/users/**").uri("lb://users-service"))
                .route("bank-account", p -> p.path("/bank-account/**").uri("lb://bank-account"))
                .route("crypto-wallet", p -> p.path("/crypto-wallet/**").uri("lb://crypto-wallet"))
                .route("crypto-exchange", p -> p.path("/crypto-exchange/**").uri("lb://crypto-exchange"))
                .route("trade-service", p -> p.path("/trade-service/**").uri("lb://trade-service"))
                .build();
    }
}