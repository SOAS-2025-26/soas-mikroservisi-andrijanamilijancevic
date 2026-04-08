package serviceLibrary.dto.currencyExchange;

import java.util.Date;

public class SingleCurrencyStructure {

	private String code;
	private String alphaCode;
	private String numericCode;
	private String name;
	private double rate;
	private Date date;
	private double inverseRate;

	public SingleCurrencyStructure(String code, String alphaCode, String numericCode, String name, double rate,
			Date date, double inverseRate) {
		super();
		this.code = code;
		this.alphaCode = alphaCode;
		this.numericCode = numericCode;
		this.name = name;
		this.rate = rate;
		this.date = date;
		this.inverseRate = inverseRate;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getAlphaCode() {
		return alphaCode;
	}

	public void setAlphaCode(String alphaCode) {
		this.alphaCode = alphaCode;
	}

	public String getNumericCode() {
		return numericCode;
	}

	public void setNumericCode(String numericCode) {
		this.numericCode = numericCode;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getRate() {
		return rate;
	}

	public void setRate(double rate) {
		this.rate = rate;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public double getInverseRate() {
		return inverseRate;
	}

	public void setInverseRate(double inverseRate) {
		this.inverseRate = inverseRate;
	}

}
