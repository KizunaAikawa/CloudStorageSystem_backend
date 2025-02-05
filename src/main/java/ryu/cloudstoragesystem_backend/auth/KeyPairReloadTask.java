package ryu.cloudstoragesystem_backend.auth;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Slf4j
public class KeyPairReloadTask {
    private final KeyPairProvider keyPairProvider;

    @Autowired
    public KeyPairReloadTask(KeyPairProvider keyPairProvider) {
        this.keyPairProvider = keyPairProvider;
    }

    @Scheduled(fixedRateString = "${password.key.valid-time}")
    public void reload() {
        keyPairProvider.reload();
        log.info("Password key pair reloaded at {}", LocalDateTime.now());
    }
}
