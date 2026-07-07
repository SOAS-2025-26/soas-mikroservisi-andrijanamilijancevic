package currencyConversion.implementation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;

import feign.FeignException;
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

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public ResponseEntity<?> currencyConversion(String from, String to, double quantity, String email) {
        CurrencyExchangeDto response;
        try {
            response = proxy.getExchangeFeign(from, to).getBody();
        } catch (FeignException e) {
            throw new InvalidCurrencyException("You've entered invalid currency pair");
        }

        BankAccountDto sourceAccount;
        try {
            sourceAccount = mapper.convertValue(
                    bankAccountProxy.getAccountByEmailAndCurrency(email, from).getBody(),
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
        bankAccountProxy.debitAccount(new BankAccountDto(email, from, quantity));

        // uvecaj (ili prvi put kreiraj) ciljnu valutu
        BankAccountDto updatedTarget = mapper.convertValue(
                bankAccountProxy.creditAccount(new BankAccountDto(email, to, exchangedAmount)).getBody(),
                BankAccountDto.class);

        String message = String.format("Uspešno je izvršena razmena %s: %.2f za %s: %.2f",
                from.toUpperCase(), quantity, to.toUpperCase(), exchangedAmount);

        CurrencyConversionDto finalResponse = new CurrencyConversionDto(
                response, quantity, exchangedAmount, message, updatedTarget);

        return ResponseEntity.ok(finalResponse);
    }
}