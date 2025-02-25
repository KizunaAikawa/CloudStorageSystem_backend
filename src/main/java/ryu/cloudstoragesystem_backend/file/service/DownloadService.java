package ryu.cloudstoragesystem_backend.file.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ryu.cloudstoragesystem_backend.file.CloudFile;
import ryu.cloudstoragesystem_backend.file.CloudFileDAO;
import ryu.cloudstoragesystem_backend.file.DownloadedFile;
import ryu.cloudstoragesystem_backend.file.ShareCodePool;
import ryu.cloudstoragesystem_backend.file.exception.UploadedFileNotFoundException;
import ryu.cloudstoragesystem_backend.user.User;

import java.nio.file.Path;
import java.time.LocalDateTime;

@Service
@Slf4j
public class DownloadService {
    @Value("${file.root-path}")
    private String fileRootPath;

    private final CloudFileDAO cloudFileDAO;

    private final RedisTemplate<String, Integer> redisTemplate;

    private final ShareCodePool shareCodePool;

    @Autowired
    public DownloadService(CloudFileDAO cloudFileDAO, RedisTemplate<String, Integer> redisTemplate,ShareCodePool shareCodePool) {
        this.cloudFileDAO = cloudFileDAO;
        this.redisTemplate = redisTemplate;
        this.shareCodePool = shareCodePool;
    }

    @Transactional
    public DownloadedFile download(User user, CloudFile file) {
        if (file.getRemovedFlag()) {
            throw new UploadedFileNotFoundException();
        }
        String redisKey = "file:" + file.getFileId();
        Integer fileMaxUsage = redisTemplate.opsForValue().get(redisKey);
        try {
            Path filePath = Path.of(fileRootPath, file.getMD5());
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists()) {
                log.info("User {} downloaded file{}.{} at {}", user.getUserId(), file.getFileName(), file.getExtension(), LocalDateTime.now());

                //减少文件可用次数
                if (fileMaxUsage != -1) {
                    redisTemplate.opsForValue().decrement(redisKey);

                    //如果减少前为1，则标记为删除
                    if (fileMaxUsage == 1) {
                        shareCodePool.release(file.getShareCode());
                        file.setRemovedFlag(true);
                        file.setShareCode(null);
                        cloudFileDAO.save(file);
                        redisTemplate.delete(redisKey);
                    }
                }
                return new DownloadedFile(file.getFileName(), file.getExtension(), resource.contentLength(), resource);
            } else throw new UploadedFileNotFoundException();
        } catch (Exception e) {
            throw new UploadedFileNotFoundException();
        }
    }

    @Transactional
    public DownloadedFile download(User user, String shareCode) {
        CloudFile file = cloudFileDAO.findByShareCode(shareCode).orElseThrow(UploadedFileNotFoundException::new);
        return download(user, file);
    }


}
