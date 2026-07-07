package cryptoWallet;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import jakarta.transaction.Transactional;
import java.util.List;

@Repository
public interface CryptoWalletRepository extends JpaRepository<CryptoWalletModel, Integer> {

    List<CryptoWalletModel> findByEmailIgnoreCase(String email);

    CryptoWalletModel findByEmailIgnoreCaseAndCurrencyCodeIgnoreCase(String email, String currencyCode);

    @Modifying
    @Transactional
    @Query("delete from CryptoWalletModel c where lower(c.email) = lower(?1)")
    void deleteByEmail(String email);

    @Modifying
    @Transactional
    @Query("update CryptoWalletModel c set c.amount=?3 where lower(c.email)=lower(?1) and lower(c.currencyCode)=lower(?2)")
    void updateAmount(String email, String currencyCode, double amount);
}