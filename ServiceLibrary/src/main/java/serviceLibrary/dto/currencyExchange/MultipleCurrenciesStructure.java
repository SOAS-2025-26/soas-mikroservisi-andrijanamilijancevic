package serviceLibrary.dto.currencyExchange;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnySetter;

public class MultipleCurrenciesStructure {

	@JsonAnySetter
	private Map<String, SingleCurrencyStructure> currencies = 
			new HashMap<>();

	public MultipleCurrenciesStructure(Map<String, SingleCurrencyStructure> currencies) {
		super();
		this.currencies = currencies;
	}

	public Map<String, SingleCurrencyStructure> getCurrencies() {
		return currencies;
	}

}
