package ryu.cloudstoragesystem_backend.file.controller;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ryu.cloudstoragesystem_backend.auth.service.AuthService;
import ryu.cloudstoragesystem_backend.file.DownloadedFile;
import ryu.cloudstoragesystem_backend.file.service.FileService;
import ryu.cloudstoragesystem_backend.user.User;

import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@RestController
public class FileController {
    private final FileService fileService;
    private final AuthService authService;

    @Autowired
    public FileController(FileService fileService, AuthService authService) {
        this.fileService = fileService;
        this.authService = authService;
    }

    @PutMapping("/file/upload")
    public String upload(@RequestHeader("Authorization") @NotBlank String token,
                         @RequestParam("share-code") @NotBlank @Pattern(regexp = "^\\d{4}$") String shareCode,
                         @NotNull MultipartFile file) {
        User user = authService.getPresentUser(token);
        fileService.upload(user, file, shareCode);
        return shareCode;
    }

    @GetMapping("/file/download")
    public void download(@RequestHeader("Authorization") @NotBlank String token,
                         @RequestParam("share-code") @NotBlank @Pattern(regexp = "^\\d{4}$") String shareCode,
                         HttpServletResponse response) throws IOException {
        User user = authService.getPresentUser(token);
        DownloadedFile downloadedFile = fileService.download(user, shareCode);
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
