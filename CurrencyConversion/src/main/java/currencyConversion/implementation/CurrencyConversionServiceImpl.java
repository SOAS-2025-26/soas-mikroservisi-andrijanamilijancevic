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
        // dohvati kurs
        CurrencyExchangeDto response = null;
        try {
            response = proxy.getExchangeFeign(from, to).getBody();
        } catch (FeignException e) {
            throw new InvalidCurrencyException("You've entered invalid currency pair");
        }

        // dohvati bankovni racun korisnika
        BankAccountDto account;
        try {
            account = mapper.convertValue(
                    bankAccountProxy.getAccountByEmail(email).getBody(), BankAccountDto.class);
        } catch (FeignException.NotFound e) {
            return ResponseEntity.status(404)
                    .body("Bank account with email: " + email + " not found!");
        }

        // proveri da li ima dovoljno sredstava
        if (!account.getCurrencyCode().equalsIgnoreCase(from)) {
            return ResponseEntity.status(400)
                    .body("Account currency is " + account.getCurrencyCode() 
                    + " but requested conversion from " + from);
        }

        if (account.getAmount() < quantity) {
            return ResponseEntity.status(400)
                    .body("Insufficient funds! Available: " + account.getAmount() 
                    + " " + from + ", requested: " + quantity);
        }

        double exchangedAmount = quantity * response.getRate();

        // umanji sredstva sa racuna
        bankAccountProxy.updateAccount(
                new BankAccountDto(email, from, account.getAmount() - quantity));

        // dohvati azurirani racun
        BankAccountDto updatedAccount = mapper.convertValue(
                bankAccountProxy.getAccountByEmail(email).getBody(), BankAccountDto.class);

        String message = String.format("Uspešno je izvršena razmena %s: %.2f za %s: %.2f",
                from.toUpperCase(), quantity, to.toUpperCase(), exchangedAmount);

        CurrencyConversionDto finalResponse = new CurrencyConversionDto(
                response, quantity, exchangedAmount, message);

        return ResponseEntity.ok(finalResponse);
    }
}