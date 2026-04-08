package serviceLibrary.dto.currencyExchange;

public class CurrencyExchangeDto {

	private String currencyFrom;
	private String currencyToCode;
	private String currencyToName;
	private double rate;

	public CurrencyExchangeDto(String currencyFrom, String currencyToCode, String currencyToName, double rate) {
		super();
		this.currencyFrom = currencyFrom;
		this.currencyToCode = currencyToCode;
		this.currencyToName = currencyToName;
		this.rate = rate;
	}

	public String getCurrencyFrom() {
		return currencyFrom;
	}

	public void setCurrencyFrom(String currencyFrom) {
		this.currencyFrom = currencyFrom;
	}

	public String getCurrencyToCode() {
		return currencyToCode;
	}

	public void setCurrencyToCode(String currencyToCode) {
		this.currencyToCode = currencyToCode;
	}

	public String getCurrencyToName() {
		return currencyToName;
	}

	public void setCurrencyToName(String currencyToName) {
		this.currencyToName = currencyToName;
	}

	public double getRate() {
		return rate;
	}

	public void setRate(double rate) {
		this.rate = rate;
	}

}
