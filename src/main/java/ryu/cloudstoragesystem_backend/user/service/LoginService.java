package ryu.cloudstoragesystem_backend.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import ryu.cloudstoragesystem_backend.auth.KeyPairProvider;
import ryu.cloudstoragesystem_backend.auth.TokenProvider;
import ryu.cloudstoragesystem_backend.auth.exception.LoginFailException;
import ryu.cloudstoragesystem_backend.user.User;
import ryu.cloudstoragesystem_backend.user.UserDAO;
import ryu.cloudstoragesystem_backend.user.exception.UserNotExistException;

import java.util.concurrent.TimeUnit;

@Service
public class LoginService {
    private final UserDAO userDAO;
    private final TokenProvider tokenProvider;
    private final RedisTemplate<String, String> redisStringTemplate;
    private final KeyPairProvider keyPairProvider;

    @Autowired
    public LoginService(UserDAO userDAO, TokenProvider tokenProvider, RedisTemplate<String, String> redisStringTemplate, KeyPairProvider keyPairProvider) {
        this.userDAO = userDAO;
        this.tokenProvider = tokenProvider;
        this.redisStringTemplate = redisStringTemplate;
        this.keyPairProvider = keyPairProvider;
    }

    public String login(String username, String password) {
        String rawPassword = keyPairProvider.decrypt(password);
        User user = userDAO.findByUsername(username).orElseThrow(UserNotExistException::new);
        if (user.getPassword().equals(rawPassword)) {
            String token = tokenProvider.generateToken(user);
            redisStringTemplate.opsForValue().set(username, token, tokenProvider.getTokenExpiration(), TimeUnit.MILLISECONDS);
            return token;
        } else throw new LoginFailException();
    }
}
