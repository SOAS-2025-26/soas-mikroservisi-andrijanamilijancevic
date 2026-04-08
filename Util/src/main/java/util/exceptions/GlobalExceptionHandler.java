package util.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(InvalidCurrencyException.class)
	ResponseEntity<?> invalidCurrencyHandler(InvalidCurrencyException ex){
		return ResponseEntity.badRequest().body(
				new ErrorModel(ex.getMessage(),
			"Please visit https://www.floatrates.com for available currencies",
			HttpStatus.BAD_REQUEST));
	}
}
