package ryu.cloudstoragesystem_backend.file;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;

@Component
@Slf4j
public class FileCleanupTask {
    @Value("${file.valid-time}")
    private Long validTime;

    @Value("${file.root-path}")
    private String fileRootPath;

    private final CloudFileDAO cloudFileDAO;

    @Autowired
    public FileCleanupTask(CloudFileDAO cloudFileDAO) {
        this.cloudFileDAO = cloudFileDAO;
    }

    @Scheduled(fixedRateString = "${file.cleanup-interval}")
    public void cleanup() {
        long now = System.currentTimeMillis();
        List<CloudFile> files = cloudFileDAO.findByTimeStampBefore(now - validTime);
        files.forEach(f -> {
            if (!f.getRemovedFlag()){
                f.setRemovedFlag(true);
                cloudFileDAO.save(f);
            }else {
                Path path = Paths.get(fileRootPath + f.getMD5());
                try {
                    Files.delete(path);
                    log.info("File {} delete success at {}", f.getFileName() + f.getExtension(), LocalDateTime.now());
                } catch (IOException e) {
                    log.warn("File {} delete fail at {}, check the file root directory!", f.getFileName() + f.getExtension(), LocalDateTime.now());
                }
            }
        });
        int count = cloudFileDAO.deleteByRemovedFlag(true);
        log.info("File cleanup task completed at {}, {} file(s) deleted.", LocalDateTime.now(), count);
    }
}
