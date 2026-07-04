package serviceLibrary.proxies;

import org.springframework.cloud.openfeign.FeignClient;

import serviceLibrary.services.usersService.UsersService;

@FeignClient(name = "users-service")
public interface UsersServiceProxy extends UsersService {
}