package currencyConversion.implementation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;

import feign.FeignException;
import jakarta.servlet.http.HttpServletRequest;
import serviceLibrary.dto.bankAccount.BankAccountDto;
import serviceLibrary.dto.currencyConversion.CurrencyConversionDto;
import serviceLibrary.dto.currencyExchange.CurrencyExchangeDto;
import serviceLibrary.proxies.BankAccountProxy;
import serviceLibrary.proxies.CurrencyExchangeProxy;
import serviceLibrary.services.currencyConversion.CurrencyConversionService;
import util.exceptions.InvalidCurrencyException;

@RestController
public class CurrencyConversionServiceImpl implements CurrencyConversionService {

    @Autowired
    private CurrencyExchangeProxy proxy;

    @Autowired
    private BankAccountProxy bankAccountProxy;
    
    @Autowired
    private HttpServletRequest request;

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public ResponseEntity<?> currencyConversion(String from, String to, double quantity, String email) {
        // Ako email nije prosleđen kroz parametar, uzmi ga iz X-User-Email zaglavlja
        String activeEmail = (email != null && !email.trim().isEmpty()) ? email : request.getHeader("X-User-Email");
        
        if (activeEmail == null) {
            return ResponseEntity.status(400).body("User email is missing in the request context!");
        }

        CurrencyExchangeDto response;
        try {
            response = proxy.getExchangeFeign(from, to).getBody();
        } catch (FeignException e) {
            throw new InvalidCurrencyException("You've entered invalid currency pair");
        }

        BankAccountDto sourceAccount;
        try {
            sourceAccount = mapper.convertValue(
                    bankAccountProxy.getAccountByEmailAndCurrency(activeEmail, from).getBody(),
                    BankAccountDto.class);
        } catch (FeignException.NotFound e) {
            return ResponseEntity.status(404)
                    .body("You don't have a " + from.toUpperCase() + " balance to convert from!");
        }

        if (sourceAccount.getAmount() < quantity) {
            return ResponseEntity.status(400)
                    .body("Insufficient funds! Available: " + sourceAccount.getAmount()
                            + " " + from + ", requested: " + quantity);
        }

        double exchangedAmount = quantity * response.getRate();

        // umanji izvornu valutu
        bankAccountProxy.debitAccount(new BankAccountDto(activeEmail, from, quantity));

        // uvecaj (ili prvi put kreiraj) ciljnu valutu
        BankAccountDto updatedTarget = mapper.convertValue(
                bankAccountProxy.creditAccount(new BankAccountDto(activeEmail, to, exchangedAmount)).getBody(),
                BankAccountDto.class);

        String message = String.format("Uspešno je izvršena razmena %s: %.2f za %s: %.2f",
                from.toUpperCase(), quantity, to.toUpperCase(), exchangedAmount);

        CurrencyConversionDto finalResponse = new CurrencyConversionDto(
                response, quantity, exchangedAmount, message, updatedTarget);

        return ResponseEntity.ok(finalResponse);
    }
}