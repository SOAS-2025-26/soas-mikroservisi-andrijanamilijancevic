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
				//http://localHost:8765/currency-exchange
				.route(p -> p.path("/currency-exchange").uri("lb://currency-exchange"))
				.route(p -> p.path("/currency-conversion").uri("lb://currency-conversion"))
				.build();
	}
}
