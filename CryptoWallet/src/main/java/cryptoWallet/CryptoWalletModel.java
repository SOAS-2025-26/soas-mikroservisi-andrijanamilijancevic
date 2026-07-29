package cryptoWallet;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "crypto_wallet", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"email", "currencyCode"})
})
public class CryptoWalletModel {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String currencyCode;

    @Column(nullable = false)
    private double amount;

    public CryptoWalletModel() {
    }

    public CryptoWalletModel(String email, String currencyCode, double amount) {
        this.email = email;
        this.currencyCode = currencyCode;
        this.amount = amount;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getCurrencyCode() { return currencyCode; }
    public void setCurrencyCode(String currencyCode) { this.currencyCode = currencyCode; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
}