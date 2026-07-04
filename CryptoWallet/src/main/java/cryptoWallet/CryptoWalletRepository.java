package cryptoWallet;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import jakarta.transaction.Transactional;

@Repository
public interface CryptoWalletRepository extends JpaRepository<CryptoWalletModel, Integer> {

    CryptoWalletModel findByEmailIgnoreCase(String email);

    @Modifying
    @Transactional
    @Query("delete from CryptoWalletModel c where c.email=?1")
    void deleteByEmail(String email);

    @Modifying
    @Transactional
    @Query("update CryptoWalletModel c set c.currencyCode=?2, c.amount=?3 where c.email=?1")
    void updateWallet(String email, String currencyCode, double amount);
}