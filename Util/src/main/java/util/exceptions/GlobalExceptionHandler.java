package util.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import feign.FeignException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidCurrencyException.class)
    ResponseEntity<?> invalidCurrencyHandler(InvalidCurrencyException ex) {
        return ResponseEntity.badRequest().body(
                new ErrorModel(ex.getMessage(),
                        "Please visit https://www.floatrates.com for available currencies",
                        HttpStatus.BAD_REQUEST));
    }

    // Kada Feign poziv ka drugom mikroservisu vrati 404 (npr. racun/wallet za trazenu valutu ne postoji)
    @ExceptionHandler(FeignException.NotFound.class)
    ResponseEntity<?> feignNotFoundHandler(FeignException.NotFound ex) {
        String errorMsg = ex.contentUTF8();
        if (errorMsg == null || errorMsg.trim().isEmpty()) {
            errorMsg = "Requested resource was not found on a dependent service.";
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                new ErrorModel(
                        errorMsg,
                        "Check that the email/currency combination you provided actually exists.",
                        HttpStatus.NOT_FOUND));
    }

    // Sve ostale greske koje stignu preko Feign poziva (400, 409, 503...)
    @ExceptionHandler(FeignException.class)
    ResponseEntity<?> feignHandler(FeignException ex) {
        HttpStatus status = HttpStatus.resolve(ex.status());
        if (status == null) {
            status = HttpStatus.SERVICE_UNAVAILABLE;
        }
        
        String errorMsg = ex.contentUTF8();
        if (errorMsg == null || errorMsg.trim().isEmpty()) {
            errorMsg = "A dependent service returned an error: " + status.getReasonPhrase();
        }
        
        return ResponseEntity.status(status).body(
                new ErrorModel(
                        errorMsg,
                        "Please check your request parameters and try again.",
                        status));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    ResponseEntity<?> illegalArgumentHandler(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(
                new ErrorModel(ex.getMessage(),
                        "Please check the values you provided in the request.",
                        HttpStatus.BAD_REQUEST));
    }

    @ExceptionHandler(NullPointerException.class)
    ResponseEntity<?> nullPointerHandler(NullPointerException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                new ErrorModel(
                        "A required value was missing from the request or from a dependent service response.",
                        "Please check that all required fields were provided.",
                        HttpStatus.BAD_REQUEST));
    }

    // Poslednja linija odbrane - hvata sve sto nije gore pokriveno, umesto da procuri kao stack trace
    @ExceptionHandler(Exception.class)
    ResponseEntity<?> genericHandler(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                new ErrorModel(
                        "An unexpected error occurred: " + ex.getMessage(),
                        "Please try again later or contact support if the problem persists.",
                        HttpStatus.INTERNAL_SERVER_ERROR));
    }
}