package ryu.cloudstoragesystem_backend.auth.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ryu.cloudstoragesystem_backend.auth.Token;
import ryu.cloudstoragesystem_backend.auth.TokenDAO;
import ryu.cloudstoragesystem_backend.auth.exception.TokenUnavailableException;
import ryu.cloudstoragesystem_backend.user.User;

import java.util.Optional;

@Service
public class AuthService {
    @Value("${token.valid-time}")
    private Long tokenValidTime;

    private final TokenDAO tokenDAO;

    @Autowired
    public AuthService(TokenDAO tokenDAO) {
        this.tokenDAO = tokenDAO;
    }

    public User getPresentUser(String tokenValue) {
        //检测token有效性
        Token token = tokenDAO.findByTokenValue(tokenValue).orElseThrow(TokenUnavailableException::new);
        //检测token是否过期
        if (token.getTimeStamp() + tokenValidTime > System.currentTimeMillis()) {
            //检测用户有效性
            return tokenDAO.findUserByTokenValue(tokenValue).orElseThrow(TokenUnavailableException::new);
        } else throw new TokenUnavailableException();
    }

    public boolean isLogin(String username) {
        Optional<Token> tokenOptional = tokenDAO.findByUserName(username);
        if (tokenOptional.isPresent()) {
            Token token = tokenOptional.get();
            if (token.getTimeStamp() + tokenValidTime > System.currentTimeMillis()) {
                return true;
            } else {
                tokenDAO.delete(token);
                return false;
            }
        } else return false;
    }
}
