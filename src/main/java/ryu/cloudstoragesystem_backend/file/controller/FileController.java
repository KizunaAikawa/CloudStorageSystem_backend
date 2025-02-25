package ryu.cloudstoragesystem_backend.file.controller;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ryu.cloudstoragesystem_backend.auth.service.AuthService;
import ryu.cloudstoragesystem_backend.file.DownloadedFile;
import ryu.cloudstoragesystem_backend.file.service.DownloadService;
import ryu.cloudstoragesystem_backend.file.service.UploadService;
import ryu.cloudstoragesystem_backend.user.User;

import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@RestController
public class FileController {
    private final UploadService uploadService;
    private final DownloadService downloadService;
    private final AuthService authService;

    @Autowired
    public FileController(UploadService uploadService, DownloadService downloadService, AuthService authService, RedisTemplate<String, String> redisTemplate) {
        this.uploadService = uploadService;
        this.downloadService = downloadService;
        this.authService = authService;
    }

    @PutMapping("/file/old_upload")
    public String oldUpload(@RequestHeader("Authorization") @NotBlank String token,
                            @RequestParam("share-code") @NotBlank @Pattern(regexp = "^\\d{4}$") String shareCode,
                            @NotNull MultipartFile file) {
        User user = authService.getPresentUser(token);
        uploadService.upload(user, file, shareCode, -1);
        return shareCode;
    }

    @PutMapping("/file/upload")
    public Map<String, String> upload(@RequestHeader("Authorization") @NotBlank String token,
                                      @RequestParam(value = "share-code",required = false) @Pattern(regexp = "^[A-Z0-9]{6}$") String shareCode,
                                      @RequestParam(value = "limit", defaultValue = "1") Integer maxUsage,
                                      @NotNull MultipartFile file) {
        User user = authService.getPresentUser(token);
        if (shareCode != null && !shareCode.isEmpty()) {
            uploadService.upload(user, file, shareCode, maxUsage);
        } else {
            shareCode = uploadService.uploadWithGeneratedCode(user, file, maxUsage);
        }
        Map<String, String> responseBody = new HashMap<>();
        responseBody.put("shareCode", shareCode);
        return responseBody;
    }

    @GetMapping("/file/download")
    public void download(@RequestHeader("Authorization") @NotBlank String token,
                         @RequestParam("share-code") @NotBlank @Pattern(regexp = "^\\d{4}$") String shareCode,
                         HttpServletResponse response) throws IOException {
        User user = authService.getPresentUser(token);
        DownloadedFile downloadedFile = downloadService.download(user, shareCode);
        File file = downloadedFile.getResource().getFile();
        String fileName = downloadedFile.getFileName() + "." + downloadedFile.getExtension();
        fileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8);
        response.addHeader("File-Name", fileName);
        response.setContentLengthLong(downloadedFile.getSize());
        response.setContentType("application/octet-stream");
        //写入输出流
        try (InputStream in = new FileInputStream(file);
             OutputStream out = response.getOutputStream()) {
            byte[] buffer = new byte[1024];
            int len;
            while ((len = in.read(buffer)) > 0) {
                out.write(buffer, 0, len);
            }
            out.flush();
        }
    }


    //TODO：测试接口，提交PR之前记得删掉
    @PutMapping("/file/test_upload")
    public Map<String, String> test_upload(@RequestParam(value = "share-code",required = false) @Pattern(regexp = "^[A-Z0-9]{6}$") String shareCode,
                                           @RequestParam(value = "limit", defaultValue = "1") Integer maxUsage,
                                           @NotNull MultipartFile file) {
        User user = new User("test_user", "12345678");
        user.setUserId(Long.valueOf("252"));
        if (shareCode != null && !shareCode.isEmpty()) {
            uploadService.upload(user, file, shareCode, maxUsage);
        } else {
            shareCode = uploadService.uploadWithGeneratedCode(user, file, maxUsage);
        }
        Map<String, String> responseBody = new HashMap<>();
        responseBody.put("shareCode", shareCode);
        return responseBody;
    }

    @GetMapping("/file/test_download")
    public void test_download(@RequestParam("share-code") @NotBlank @Pattern(regexp = "^[A-Z0-9]{6}$") String shareCode,
                              HttpServletResponse response) throws IOException {
        User user = new User("test_user", "12345678");
        user.setUserId(Long.valueOf("252"));
        DownloadedFile downloadedFile = downloadService.download(user, shareCode);
        File file = downloadedFile.getResource().getFile();
        String fileName = downloadedFile.getFileName() + "." + downloadedFile.getExtension();
        fileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8);
        response.addHeader("File-Name", fileName);
        response.setContentLengthLong(downloadedFile.getSize());
        response.setContentType("application/octet-stream");
        //写入输出流
        try (InputStream in = new FileInputStream(file);
             OutputStream out = response.getOutputStream()) {
            byte[] buffer = new byte[1024];
            int len;
            while ((len = in.read(buffer)) > 0) {
                out.write(buffer, 0, len);
            }
            out.flush();
        }
    }
}
