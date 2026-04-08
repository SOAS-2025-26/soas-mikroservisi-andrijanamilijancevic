package currencyConversion.implementation;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import serviceLibrary.dto.currencyConversion.CurrencyConversionDto;
import serviceLibrary.dto.currencyExchange.CurrencyExchangeDto;
import serviceLibrary.services.currencyConversion.CurrencyConversionService;
import util.exceptions.InvalidCurrencyException;

@RestController
public class CurrencyConversionServiceImpl implements CurrencyConversionService {

	private RestTemplate rest = new RestTemplate();
	
	@Override
	public ResponseEntity<?> currencyConversion(String from, String to, double quantity) {
		String apiUrl = 
		String.format("http://localhost:8080/currency-exchange?from=%s&to=%s"
				, from.toLowerCase(), to.toLowerCase());
		
		CurrencyExchangeDto response = null;
		try {
			response=
					rest.getForEntity(apiUrl, CurrencyExchangeDto.class).getBody();
		} catch (HttpServerErrorException e) {
			throw new InvalidCurrencyException("You've entered invalid currency pair");
		}
		
		double exchangedAmount = quantity * response.getRate();
		
		CurrencyConversionDto finalResponse = 
				new CurrencyConversionDto(response, quantity, exchangedAmount,
				String.format("Converted %s %s to %s %s", quantity, from.toUpperCase(),
						exchangedAmount, to.toUpperCase()));
		return ResponseEntity.ok(finalResponse);
	}

}
