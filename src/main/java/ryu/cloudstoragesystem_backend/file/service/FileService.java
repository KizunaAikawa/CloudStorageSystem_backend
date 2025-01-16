package ryu.cloudstoragesystem_backend.file.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ryu.cloudstoragesystem_backend.file.CloudFile;
import ryu.cloudstoragesystem_backend.file.CloudFileDAO;
import ryu.cloudstoragesystem_backend.file.DownloadedFile;
import ryu.cloudstoragesystem_backend.file.exception.UploadedFileNotFoundException;
import ryu.cloudstoragesystem_backend.user.User;
import ryu.cloudstoragesystem_backend.util.MD5Util;

import java.io.File;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.time.LocalDateTime;

@Service
@Slf4j
public class FileService {
    @Value("${file.root-path}")
    private String fileRootPath;

    private final CloudFileDAO cloudFileDAO;

    @Autowired
    public FileService(CloudFileDAO cloudFileDAO) {
        this.cloudFileDAO = cloudFileDAO;
    }

    @Transactional
    public void upload(User user, MultipartFile file, String shareCode) {
        try {
            byte[] data = file.getInputStream().readAllBytes();
            //存储文件实体
            String md5 = MD5Util.getMD5(data);
            String fileName = file.getOriginalFilename();
            String extension = fileName.substring(fileName.indexOf('.') + 1);
            fileName = fileName.substring(0, fileName.indexOf('.'));
            CloudFile sharedFile = new CloudFile(fileName, extension);
            sharedFile.setMD5(md5);
            sharedFile.setShareCode(shareCode);
            sharedFile.setOwner(user);
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Todo:导入Google Map API限制范围
    public DownloadedFile download(User user, CloudFile file) {
        try {
            Path filePath = Path.of(fileRootPath, file.getMD5());
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists()) {
                log.info("User {} downloaded file{}.{} at {}", user.getUserId(), file.getFileName(), file.getExtension(), LocalDateTime.now());
                return new DownloadedFile(file.getFileName(), file.getExtension(), resource.contentLength(), resource);
            } else throw new UploadedFileNotFoundException();
        } catch (Exception e) {
            throw new UploadedFileNotFoundException();
        }
    }

    public DownloadedFile download(User user, String shareCode) {
        CloudFile file = cloudFileDAO.findByShareCode(shareCode).orElseThrow(UploadedFileNotFoundException::new);
        return download(user, file);
    }
}
