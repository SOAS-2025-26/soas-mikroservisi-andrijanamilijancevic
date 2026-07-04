package usersService;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import jakarta.transaction.Transactional;

@Repository
public interface UserRepository extends JpaRepository<UserModel, Integer> {

    UserModel findByEmailIgnoreCase(String email);

    @Modifying
    @Transactional
    @Query("update UserModel u set u.password=?2, u.role=?3 where u.email=?1")
    void updateUser(String email, String password, String role);
}