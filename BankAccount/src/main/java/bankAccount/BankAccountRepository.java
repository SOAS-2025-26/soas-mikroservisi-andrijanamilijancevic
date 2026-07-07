package bankAccount;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import jakarta.transaction.Transactional;
import java.util.List;

@Repository
public interface BankAccountRepository extends JpaRepository<BankAccountModel, Integer> {

    List<BankAccountModel> findByEmailIgnoreCase(String email);

    BankAccountModel findByEmailIgnoreCaseAndCurrencyCodeIgnoreCase(String email, String currencyCode);

    @Modifying
    @Transactional
    @Query("delete from BankAccountModel b where lower(b.email) = lower(?1)")
    void deleteByEmail(String email);
}