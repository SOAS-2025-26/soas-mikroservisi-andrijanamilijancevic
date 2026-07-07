package cryptoExchange;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import serviceLibrary.dto.cryptoExchange.CryptoExchangeDto;
import serviceLibrary.services.cryptoExchange.CryptoExchangeService;

import java.util.Map;

@RestController
public class CryptoExchangeServiceImplementation implements CryptoExchangeService {

    private static final Map<String, String> CRYPTO_IDS = Map.of(
            "BTC", "bitcoin",
            "ETH", "ethereum",
            "SOL", "solana",
            "ADA", "cardano",
            "DOGE", "dogecoin",
            "XRP", "ripple",
            "DOT", "polkadot",
            "LTC", "litecoin"
    );

    private final WebClient webClient = WebClient.builder().build();

    @Override
    public ResponseEntity<?> getExchangeRate(String from, String to) {
        String cryptoId = CRYPTO_IDS.get(from.toUpperCase());
        if (cryptoId == null) {
            return ResponseEntity.status(400)
                    .body("Unsupported crypto currency: " + from);
        }

        String targetCurrency = to.toLowerCase();
        String url = "https://api.coingecko.com/api/v3/simple/price?ids="
                + cryptoId + "&vs_currencies=" + targetCurrency;

        try {
            Map<String, Map<String, Object>> response = webClient.get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            if (response == null || !response.containsKey(cryptoId)) {
                return ResponseEntity.status(400)
                        .body("Could not fetch rate for: " + from + " to " + to);
            }

            Number rate = (Number) response.get(cryptoId).get(targetCurrency);
            if (rate == null) {
                return ResponseEntity.status(400)
                        .body("Unsupported target currency: " + to);
            }

            return ResponseEntity.ok(new CryptoExchangeDto(
                    from.toUpperCase(), to.toUpperCase(), rate.doubleValue()));

        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body("Error fetching crypto exchange rate: " + e.getMessage());
        }
    }
}