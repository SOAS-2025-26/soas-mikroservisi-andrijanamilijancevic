package serviceLibrary.dto.cryptoWallet;

public class CryptoWalletDto {

    private String email;
    private String currencyCode;
    private double amount;

    public CryptoWalletDto() {
    }

    public CryptoWalletDto(String email, String currencyCode, double amount) {
        this.email = email;
        this.currencyCode = currencyCode;
        this.amount = amount;
    }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getCurrencyCode() { return currencyCode; }
    public void setCurrencyCode(String currencyCode) { this.currencyCode = currencyCode; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
}