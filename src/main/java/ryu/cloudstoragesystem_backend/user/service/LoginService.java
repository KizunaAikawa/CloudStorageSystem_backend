package ryu.cloudstoragesystem_backend.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ryu.cloudstoragesystem_backend.auth.Token;
import ryu.cloudstoragesystem_backend.auth.TokenDAO;
import ryu.cloudstoragesystem_backend.auth.exception.LoginFailException;
import ryu.cloudstoragesystem_backend.user.User;
import ryu.cloudstoragesystem_backend.user.UserDAO;
import ryu.cloudstoragesystem_backend.user.exception.UserNotExistException;

@Service
public class LoginService {
    private final TokenDAO tokenDAO;
    private final UserDAO userDAO;

    @Autowired
    public LoginService(TokenDAO tokenDAO, UserDAO userDAO) {
        this.tokenDAO = tokenDAO;
        this.userDAO = userDAO;
    }

    public String login(String username, String password) {
        User user = userDAO.findByUsername(username).orElseThrow(UserNotExistException::new);
        if (user.getPassword().equals(password)) {
            Token token = new Token(user);
            tokenDAO.save(token);
            return token.getTokenValue();
        } else throw new LoginFailException();
    }
}
