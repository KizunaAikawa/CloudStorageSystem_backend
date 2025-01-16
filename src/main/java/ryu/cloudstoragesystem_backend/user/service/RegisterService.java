package ryu.cloudstoragesystem_backend.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ryu.cloudstoragesystem_backend.auth.Token;
import ryu.cloudstoragesystem_backend.auth.TokenDAO;
import ryu.cloudstoragesystem_backend.user.User;
import ryu.cloudstoragesystem_backend.user.UserDAO;
import ryu.cloudstoragesystem_backend.user.exception.UsernameConflictException;

@Service
public class RegisterService {
    private final TokenDAO tokenDAO;
    private final UserDAO userDAO;

    @Autowired
    public RegisterService(TokenDAO tokenDAO, UserDAO userDAO) {
        this.tokenDAO = tokenDAO;
        this.userDAO = userDAO;
    }

    public String register(User user) {
        if (!userDAO.existsByUsername(user.getUsername())) {
            Token token = new Token(user);
            userDAO.save(user);
            tokenDAO.save(token);
            return token.getTokenValue();
        } else throw new UsernameConflictException();
    }

    public String register(String username, String password) {
        if (!userDAO.existsByUsername(username)) {
            User user = new User(username, password);
            Token token = new Token(user);
            userDAO.save(user);
            tokenDAO.save(token);
            return token.getTokenValue();
        } else throw new UsernameConflictException();
    }
}
