package serviceLibrary.services.currencyConversion;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Service
public interface CurrencyConversionService {

	@GetMapping("/currency-conversion")
	ResponseEntity<?> currencyConversion(@RequestParam String from,
			@RequestParam String to,
			@RequestParam double quantity);
}
