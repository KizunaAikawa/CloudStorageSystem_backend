package ryu.cloudstoragesystem_backend.file;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface CloudFileDAO extends JpaRepository<CloudFile, Long> {
    Optional<CloudFile> findByShareCode(String shareCode);

    List<CloudFile> findByTimeStampBefore(Long timeStamp);

    @Transactional
    int deleteByTimeStampBefore(Long timeStamp);
}
