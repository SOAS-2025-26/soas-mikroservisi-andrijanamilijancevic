package serviceLibrary.dto.currencyConversion;

import serviceLibrary.dto.currencyExchange.CurrencyExchangeDto;

public class CurrencyConversionDto {

	private CurrencyExchangeDto exchange;
	private double quantity;
	private double exchangedAmount;
	private String message;

	public CurrencyConversionDto(CurrencyExchangeDto exchange, double quantity, double exchangedAmount,
			String message) {
		super();
		this.exchange = exchange;
		this.quantity = quantity;
		this.exchangedAmount = exchangedAmount;
		this.message = message;
	}

	public CurrencyExchangeDto getExchange() {
		return exchange;
	}

	public void setExchange(CurrencyExchangeDto exchange) {
		this.exchange = exchange;
	}

	public double getQuantity() {
		return quantity;
	}

	public void setQuantity(double quantity) {
		this.quantity = quantity;
	}

	public double getExchangedAmount() {
		return exchangedAmount;
	}

	public void setExchangedAmount(double exchangedAmount) {
		this.exchangedAmount = exchangedAmount;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
