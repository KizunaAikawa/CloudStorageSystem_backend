package ryu.cloudstoragesystem_backend.auth;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ryu.cloudstoragesystem_backend.ServerErrorException;
import ryu.cloudstoragesystem_backend.user.exception.PasswordUnavailableException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.*;
import java.util.Base64;

@Component
@Slf4j
public class KeyPairProvider {
    @Value("${password.key-size}")
    private int KEY_SIZE = 2048;

    private KeyPair keyPair;
    private final KeyPairGenerator keyPairGenerator;


    public KeyPairProvider() throws NoSuchAlgorithmException {
        keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(KEY_SIZE);
    }

    public PublicKey getPublicKey() {
        if (keyPair == null) {
            keyPair = keyPairGenerator.generateKeyPair();
        }
        return keyPair.getPublic();
    }

    public void reload() {
        this.keyPair = keyPairGenerator.generateKeyPair();
    }

    public String decrypt(String encrypted) {
        try {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, keyPair.getPrivate());
            byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encrypted));
            return new String(decryptedBytes);
        } catch (NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException e) {
            throw new PasswordUnavailableException();
        } catch (NoSuchAlgorithmException e) {
            log.error("No such algorithm, check application.properties!");
            throw new ServerErrorException();
        } catch (InvalidKeyException e) {
            log.error("Secret key is invalid!");
            throw new ServerErrorException();
        }
    }
}
