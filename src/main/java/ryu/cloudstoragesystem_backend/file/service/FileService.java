package ryu.cloudstoragesystem_backend.file.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ryu.cloudstoragesystem_backend.BadRequestParamException;
import ryu.cloudstoragesystem_backend.ServerErrorException;
import ryu.cloudstoragesystem_backend.file.CloudFile;
import ryu.cloudstoragesystem_backend.file.CloudFileDAO;
import ryu.cloudstoragesystem_backend.file.DownloadedFile;
import ryu.cloudstoragesystem_backend.file.ShareCodePool;
import ryu.cloudstoragesystem_backend.file.exception.UploadedFileNotFoundException;
import ryu.cloudstoragesystem_backend.user.User;
import ryu.cloudstoragesystem_backend.util.MD5Util;

import java.io.File;
import java.io.IOException;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class FileService {
    @Value("${file.root-path}")
    private String fileRootPath;

    @Value("${file.valid-time}")
    private long fileValidTime;

    private final CloudFileDAO cloudFileDAO;

    private final RedisTemplate<String, Integer> redisTemplate;

    private final ShareCodePool shareCodePool;

    @Transactional
    public void upload(User user, MultipartFile file, String shareCode, Integer maxUsage
    ) {
        try {
            byte[] data = file.getInputStream().readAllBytes();
            //存储文件实体
            if (!(data.length > 0)) {
                throw new BadRequestParamException();
            }
            String md5 = MD5Util.getMD5(data);
            String fileName = file.getOriginalFilename();
            String extension = fileName.substring(fileName.indexOf('.') + 1);
            fileName = fileName.substring(0, fileName.indexOf('.'));
            CloudFile sharedFile = new CloudFile(fileName, extension);
            sharedFile.setMD5(md5);
            sharedFile.setShareCode(shareCode);
            sharedFile.setOwner(user);
            sharedFile.setMaxUsage(maxUsage);
            cloudFileDAO.save(sharedFile);

            //存储至服务器指定目录
            Path folderPath = Path.of(fileRootPath);
            if (!folderPath.toFile().exists()) {
                folderPath.toFile().mkdirs();
            }
            Path uploadFilePath = folderPath.resolve(md5);
            if (!uploadFilePath.toFile().exists()) {
                if (uploadFilePath.toFile().createNewFile()) {
                    File newfile = new File(uploadFilePath.toRealPath(LinkOption.NOFOLLOW_LINKS).toString());
                    file.transferTo(newfile);
                    log.info("User {} uploaded file{}.{} to {} at {}", user.getUserId(), fileName, extension, uploadFilePath, LocalDateTime.now());
                }
            }

            //在Redis中缓存下载次数
            redisTemplate.opsForValue().set("file:" + sharedFile.getFileId(), maxUsage, fileValidTime, TimeUnit.MILLISECONDS);

        } catch (IOException e) {
            throw new ServerErrorException(e.getMessage());
        }
    }

    @Transactional
    public String uploadWithGeneratedCode(User user, MultipartFile file, Integer maxUsage) {
        String shareCode = shareCodePool.get();
        upload(user, file, shareCode, maxUsage);
        return shareCode;
    }

    @Autowired
    public FileService(CloudFileDAO cloudFileDAO, RedisTemplate<String, Integer> redisTemplate, ShareCodePool shareCodePool) {
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

                //减少文件可用次数，-1代表有效时间内无限次可用
                if (fileMaxUsage != -1) {
                    redisTemplate.opsForValue().decrement(redisKey);

                    //如果减少前为1，则标记为删除
                    if (fileMaxUsage == 1) {
                        markFileAsRemoved(file);
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

    @Transactional
    public void markFileAsRemoved(CloudFile file) {
        String shareCodeCache = file.getShareCode();
        file.setRemovedFlag(true);
        file.setShareCode(null);
        cloudFileDAO.save(file);
        shareCodePool.release(shareCodeCache);
        redisTemplate.delete(shareCodeCache);
    }


}
