package ryu.cloudstoragesystem_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CloudStorageSystemBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(CloudStorageSystemBackendApplication.class, args);
    }

}
