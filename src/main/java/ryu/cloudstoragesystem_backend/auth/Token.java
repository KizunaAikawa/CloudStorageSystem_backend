package ryu.cloudstoragesystem_backend.auth;

import jakarta.persistence.*;
import lombok.Data;
import ryu.cloudstoragesystem_backend.user.User;

import java.util.UUID;

@Entity
@Data
public class Token {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long tokenId;

    private String tokenValue;

    private Long timeStamp;

    @OneToOne
    private User user;

    public Token() {
    }

    public Token(User user) {
        this.user = user;
        this.tokenValue = UUID.randomUUID().toString();
        this.timeStamp = System.currentTimeMillis();
    }
}
