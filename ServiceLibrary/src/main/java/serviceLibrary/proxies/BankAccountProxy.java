package serviceLibrary.proxies;

import org.springframework.cloud.openfeign.FeignClient;

import serviceLibrary.services.bankAccount.BankAccountService;

@FeignClient(name = "bank-account")
public interface BankAccountProxy extends BankAccountService {
}