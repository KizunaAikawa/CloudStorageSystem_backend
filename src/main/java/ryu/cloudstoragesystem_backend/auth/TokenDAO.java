package ryu.cloudstoragesystem_backend.auth;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ryu.cloudstoragesystem_backend.user.User;

import java.util.Optional;

@Repository
public interface TokenDAO extends JpaRepository<Token, Long> {

    Optional<Token> findByTokenValue(String tokenValue);

    @Query("SELECT u FROM User u JOIN Token t ON u.userId = t.user.userId WHERE t.tokenValue = :token ")
    Optional<User> findUserByTokenValue(@Param("token") String token);

    @Query("SELECT t FROM Token t JOIN User u ON u.userId = t.user.userId WHERE u.username = :username")
    Optional<Token> findByUserName(@Param("username") String username);

    @Transactional
    int deleteByTimeStampBefore(Long timeStamp);
}
