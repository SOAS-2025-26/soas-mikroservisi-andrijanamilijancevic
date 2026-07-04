package serviceLibrary.proxies;

import org.springframework.cloud.openfeign.FeignClient;

import serviceLibrary.services.currencyConversion.CurrencyConversionService;

@FeignClient(name = "currency-conversion")
public interface CurrencyConversionProxy extends CurrencyConversionService {
}