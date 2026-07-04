package serviceLibrary.services.tradeService;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Service
public interface TradeService {

    @GetMapping("/trade-service")
    ResponseEntity<?> trade(@RequestParam String from,
                            @RequestParam String to,
                            @RequestParam double quantity,
                            @RequestParam String email);
}