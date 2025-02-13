package ryu.cloudstoragesystem_backend.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ryu.cloudstoragesystem_backend.BadRequestParamException;
import ryu.cloudstoragesystem_backend.auth.KeyPairProvider;
import ryu.cloudstoragesystem_backend.auth.TokenProvider;
import ryu.cloudstoragesystem_backend.user.User;
import ryu.cloudstoragesystem_backend.user.UserDAO;
import ryu.cloudstoragesystem_backend.user.exception.UsernameConflictException;

import java.util.concurrent.TimeUnit;

@Service
public class RegisterService {
    private final UserDAO userDAO;
    private final TokenProvider tokenProvider;
    private final KeyPairProvider keyPairProvider;
    private final RedisTemplate<String, String> redisStringTemplate;

    @Autowired
    public RegisterService(UserDAO userDAO, TokenProvider tokenProvider, KeyPairProvider keyPairProvider, RedisTemplate<String, String> redisStringTemplate) {
        this.userDAO = userDAO;
        this.tokenProvider = tokenProvider;
        this.keyPairProvider = keyPairProvider;
        this.redisStringTemplate = redisStringTemplate;
    }

    @Transactional
    public String register(String username, String password) throws Exception {
        if (!userDAO.existsByUsername(username)) {
            String rawPassword = keyPairProvider.decrypt(password);
            if (rawPassword.isEmpty()) {
                throw new BadRequestParamException();
            }
            User user = new User(username, rawPassword);
            userDAO.save(user);
            String token = tokenProvider.generateToken(user);
            redisStringTemplate.opsForValue().set(username, token, tokenProvider.getTokenExpiration(), TimeUnit.MILLISECONDS);
            return tokenProvider.generateToken(user);
        } else throw new UsernameConflictException();
    }

    @Transactional
    public String register(User user) throws Exception {
        return register(user.getUsername(), user.getPassword());
    }
}
