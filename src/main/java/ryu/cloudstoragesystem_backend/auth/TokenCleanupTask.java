/*package ryu.cloudstoragesystem_backend.auth;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Slf4j
public class TokenCleanupTask {
    @Value("${token.expiration}")
    private Long validTime;

    private final TokenDAO tokenDAO;

    @Autowired
    public TokenCleanupTask(TokenDAO tokenDAO) {
        this.tokenDAO = tokenDAO;
    }

    @Scheduled(fixedRateString = "${token.cleanup-interval}")
    public void cleanup() {
        long now = System.currentTimeMillis();
        int count = tokenDAO.deleteByTimeStampBefore(now - validTime);
        log.info("Token cleanup task completed at {}, {} token(s) deleted.", LocalDateTime.now(), count);
    }
}*/
