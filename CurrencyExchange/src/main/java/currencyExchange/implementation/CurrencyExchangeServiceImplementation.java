package currencyExchange.implementation;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import serviceLibrary.dto.currencyExchange.CurrencyExchangeDto;
import serviceLibrary.dto.currencyExchange.MultipleCurrenciesStructure;
import serviceLibrary.dto.currencyExchange.SingleCurrencyStructure;
import serviceLibrary.services.currencyExchange.CurrencyExchangeService;

@RestController
public class CurrencyExchangeServiceImplementation implements CurrencyExchangeService{

	private RestTemplate template = new RestTemplate();

	@Override
	public ResponseEntity<?> getExchange(String from, String to) {
		String apiUrl = 
				String.format("https://www.floatrates.com/daily/%s.json", from);
		SingleCurrencyStructure response = 
				template.getForEntity(apiUrl, MultipleCurrenciesStructure.class)
				.getBody().getCurrencies().get(to);
			
		CurrencyExchangeDto finalResponse = 
		new CurrencyExchangeDto(from.toUpperCase(), response.getCode(), response.getName(), response.getRate());
		return ResponseEntity.ok(finalResponse);
	}
	
//	@GetMapping("test")
//	public ResponseEntity<?> testAPI(){
//		ResponseEntity<Object> response = 
//				template.getForEntity("https://www.floatrates.com/daily/eur.json"
//				, Object.class);
//		return ResponseEntity.ok(response.getBody());
//	}
}
