package ryu.cloudstoragesystem_backend.file;

import jakarta.persistence.*;
import lombok.Data;
import ryu.cloudstoragesystem_backend.user.User;

@Entity
@Data
public class CloudFile {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long fileId;

    private String fileName;

    private String extension;

    @ManyToOne
    private User owner;

    private String filePath;

    private String MD5;

    private String shareCode;

    private Integer maxUsage;

    private Boolean removedFlag;

    private Long timeStamp;


    public CloudFile() {
    }

    public CloudFile(String fileName, String extension) {
        this.fileName = fileName;
        this.extension = extension;
        this.removedFlag = false;
        this.timeStamp = System.currentTimeMillis();
    }

}
