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

    private Double getSingleRateInUsd(String cryptoCode) {
        String cryptoId = CRYPTO_IDS.get(cryptoCode.toUpperCase());
        if (cryptoId == null) {
            return null;
        }
        String url = "https://api.coingecko.com/api/v3/simple/price?ids=" + cryptoId + "&vs_currencies=usd";
        try {
            Map<String, Map<String, Object>> response = webClient.get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();
            if (response != null && response.containsKey(cryptoId)) {
                Number rate = (Number) response.get(cryptoId).get("usd");
                if (rate != null) {
                    return rate.doubleValue();
                }
            }
        } catch (Exception e) {
            // Log error
        }
        return null;
    }

    @Override
    public ResponseEntity<?> getExchangeRate(String from, String to) {
        if (from == null || to == null) {
            return ResponseEntity.status(400).body("Currencies must not be null");
        }

        if (from.equalsIgnoreCase(to)) {
            return ResponseEntity.ok(new CryptoExchangeDto(from.toUpperCase(), to.toUpperCase(), 1.0));
        }

        boolean fromIsCrypto = CRYPTO_IDS.containsKey(from.toUpperCase());
        boolean toIsCrypto = CRYPTO_IDS.containsKey(to.toUpperCase());

        // 1. Ako je Crypto u Crypto (npr. BTC -> ETH)
        if (fromIsCrypto && toIsCrypto) {
            Double fromInUsd = getSingleRateInUsd(from);
            Double toInUsd = getSingleRateInUsd(to);
            if (fromInUsd == null || toInUsd == null) {
                return ResponseEntity.status(400).body("Could not fetch crypto rates.");
            }
            double finalRate = fromInUsd / toInUsd;
            return ResponseEntity.ok(new CryptoExchangeDto(from.toUpperCase(), to.toUpperCase(), finalRate));
        }

        // 2. Ako je Crypto u Fiat (npr. BTC -> USD)
        if (fromIsCrypto && !toIsCrypto) {
            String cryptoId = CRYPTO_IDS.get(from.toUpperCase());
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

        // 3. Ako je Fiat u Crypto (npr. USD -> BTC)
        if (!fromIsCrypto && toIsCrypto) {
            Double toInUsd = getSingleRateInUsd(to);
            if (toInUsd == null) {
                return ResponseEntity.status(400).body("Could not fetch crypto rate for " + to);
            }
            
            // Ako je polazna valuta npr. EUR a ne USD, moramo videti EUR u USD odnos
            double multiplier = 1.0;
            if (!from.equalsIgnoreCase("USD")) {
                // Možeš dodati fallback da se koristi fiksni kurs ili floatrates
                // Za potrebe TradeService-a, on uvek pretvara u USD/EUR pa poziva, tako da je from uglavnom USD ili EUR
                if (from.equalsIgnoreCase("EUR")) {
                    multiplier = 1.08; // priblizan EUR/USD kurs ako floatrates ne odgovori
                }
            }

            double rate = (1.0 / toInUsd) * multiplier;
            return ResponseEntity.ok(new CryptoExchangeDto(from.toUpperCase(), to.toUpperCase(), rate));
        }

        return ResponseEntity.status(400).body("At least one currency must be cryptocurrency.");
    }
}