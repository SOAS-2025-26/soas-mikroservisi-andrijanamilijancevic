package cryptoWallet;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class CryptoWalletModel {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Column(nullable = false, unique = true)
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