package bankAccount;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import jakarta.transaction.Transactional;

@Repository
public interface BankAccountRepository extends JpaRepository<BankAccountModel, Integer> {

    BankAccountModel findByEmailIgnoreCase(String email);

    @Modifying
    @Transactional
    @Query("delete from BankAccountModel b where b.email=?1")
    void deleteByEmail(String email);

    @Modifying
    @Transactional
    @Query("update BankAccountModel b set b.currencyCode=?2, b.amount=?3 where b.email=?1")
    void updateAccount(String email, String currencyCode, double amount);
}