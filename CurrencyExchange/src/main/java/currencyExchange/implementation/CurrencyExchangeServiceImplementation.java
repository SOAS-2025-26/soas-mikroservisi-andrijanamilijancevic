package currencyExchange.implementation;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import serviceLibrary.dto.currencyExchange.CurrencyExchangeDto;
import serviceLibrary.dto.currencyExchange.MultipleCurrenciesStructure;
import serviceLibrary.dto.currencyExchange.SingleCurrencyStructure;
import serviceLibrary.services.currencyExchange.CurrencyExchangeService;

@RestController
public class CurrencyExchangeServiceImplementation implements CurrencyExchangeService {

    private final WebClient webClient = WebClient.builder().build();

    @Override
    public ResponseEntity<?> getExchange(String from, String to) {
        if (from == null || to == null) {
            return ResponseEntity.status(400).body("Currencies must not be null");
        }

        // Ako su valute iste, kurs je 1.0 i nema potrebe pozivati API
        if (from.equalsIgnoreCase(to)) {
            CurrencyExchangeDto selfResponse = new CurrencyExchangeDto(
                    from.toUpperCase(), to.toUpperCase(), from.toUpperCase() + " currency", 1.0);
            return ResponseEntity.ok(selfResponse);
        }

        String apiUrl = String.format("https://www.floatrates.com/daily/%s.json", from.toLowerCase());

        try {
            MultipleCurrenciesStructure response = webClient.get()
                    .uri(apiUrl)
                    .retrieve()
                    .bodyToMono(MultipleCurrenciesStructure.class)
                    .block();

            if (response == null || response.getCurrencies() == null) {
                return ResponseEntity.status(400)
                        .body("Could not fetch rates for currency: " + from);
            }

            SingleCurrencyStructure currency = response.getCurrencies().get(to.toLowerCase());
            if (currency == null) {
                return ResponseEntity.status(400)
                        .body("Currency not found: " + to);
            }

            CurrencyExchangeDto finalResponse = new CurrencyExchangeDto(
                    from.toUpperCase(), currency.getCode(), currency.getName(), currency.getRate());

            return ResponseEntity.ok(finalResponse);

        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body("Error fetching exchange rate: " + e.getMessage());
        }
    }
}