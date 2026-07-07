package usersService;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "serviceLibrary.proxies")
@ComponentScan(basePackages = {"util.exceptions", "usersService"})
public class UsersServiceApplication {
	

	public static void main(String[] args) {
		SpringApplication.run(UsersServiceApplication.class, args);
	}

}
