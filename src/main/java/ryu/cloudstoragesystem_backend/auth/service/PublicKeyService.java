package ryu.cloudstoragesystem_backend.auth.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ryu.cloudstoragesystem_backend.auth.KeyPairProvider;

import java.util.Base64;

@Service
public class PublicKeyService {
    private final KeyPairProvider keyPairProvider;

    @Autowired
    public PublicKeyService(KeyPairProvider keyPairProvider) {
        this.keyPairProvider = keyPairProvider;
    }

    public String getPublicKey() {
        return Base64.getEncoder().encodeToString(keyPairProvider.getPublicKey().getEncoded());
    }
}
