package ryu.cloudstoragesystem_backend.file;

import lombok.Data;
import org.springframework.core.io.Resource;

@Data
public class DownloadedFile {
    private String fileName;
    private String extension;
    private long size;
    private Resource resource;

    public DownloadedFile(String fileName, String extension, long size, Resource resource) {
        this.fileName = fileName;
        this.extension = extension;
        this.size = size;
        this.resource = resource;
    }
}
