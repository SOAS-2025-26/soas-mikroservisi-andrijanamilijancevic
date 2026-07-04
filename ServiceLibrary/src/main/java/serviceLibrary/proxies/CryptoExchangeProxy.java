package serviceLibrary.proxies;

import org.springframework.cloud.openfeign.FeignClient;

import serviceLibrary.services.cryptoExchange.CryptoExchangeService;

@FeignClient(name = "crypto-exchange")
public interface CryptoExchangeProxy extends CryptoExchangeService {
}