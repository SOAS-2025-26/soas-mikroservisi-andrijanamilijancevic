package serviceLibrary.dto.cryptoExchange;

public class CryptoExchangeDto {

    private String cryptoCode;
    private String targetCurrency;
    private double rate;

    public CryptoExchangeDto() {
    }

    public CryptoExchangeDto(String cryptoCode, String targetCurrency, double rate) {
        this.cryptoCode = cryptoCode;
        this.targetCurrency = targetCurrency;
        this.rate = rate;
    }

    public String getCryptoCode() { return cryptoCode; }
    public void setCryptoCode(String cryptoCode) { this.cryptoCode = cryptoCode; }

    public String getTargetCurrency() { return targetCurrency; }
    public void setTargetCurrency(String targetCurrency) { this.targetCurrency = targetCurrency; }

    public double getRate() { return rate; }
    public void setRate(double rate) { this.rate = rate; }
}