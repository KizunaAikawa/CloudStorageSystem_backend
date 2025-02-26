package ryu.cloudstoragesystem_backend.file;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ryu.cloudstoragesystem_backend.file.service.FileService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@Slf4j
public class FileCleanupTask {
    @Value("${file.valid-time}")
    private Long validTime;

    @Value("${file.root-path}")
    private String fileRootPath;

    private final CloudFileDAO cloudFileDAO;

    private final FileService fileService;

    @Autowired
    public FileCleanupTask(CloudFileDAO cloudFileDAO, ShareCodePool shareCodePool, FileService fileService) {
        this.cloudFileDAO = cloudFileDAO;
        this.fileService = fileService;
    }

    @Scheduled(fixedRateString = "${file.cleanup-interval}")
    @Transactional
    public synchronized void cleanup() {
        long now = System.currentTimeMillis();
        AtomicInteger count = new AtomicInteger(0);
        List<CloudFile> files = cloudFileDAO.findByTimeStampBefore(now - validTime);
        files.forEach(f -> {
            //对未标记为移除的过期文件进行标记，这些文件将在下次轮询时被真正删除
            if (!f.getRemovedFlag()) {
                fileService.markFileAsRemoved(f);
            } else {
                Path path = Paths.get(fileRootPath + f.getMD5());
                try {
                    Files.delete(path);
                    log.info("File {} delete success at {}", f.getFileName() + f.getExtension(), LocalDateTime.now());
                } catch (IOException e) {
                    log.warn("File {} delete fail at {}, check the file root directory!", f.getFileName() + f.getExtension(), LocalDateTime.now());
                }
                cloudFileDAO.deleteById(f.getFileId());
                count.getAndIncrement();
            }
        });
        log.info("File cleanup task completed at {}, {} file(s) deleted.", LocalDateTime.now(), count.get());
    }
}
