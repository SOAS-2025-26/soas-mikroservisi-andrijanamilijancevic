package cryptoExchange;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class CryptoExchangeApplication {

	public static void main(String[] args) {
		SpringApplication.run(CryptoExchangeApplication.class, args);
	}

}
