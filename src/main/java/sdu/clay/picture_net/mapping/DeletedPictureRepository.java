package sdu.clay.picture_net.mapping;

import sdu.clay.picture_net.pojo.DeletedPicture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface DeletedPictureRepository extends JpaRepository<DeletedPicture, Integer> {
    @Transactional
    @Modifying
    @Query("delete from DeletedPicture deletedPicture where deletedPicture.pictureId = ?1")
    int deleteByPictureId(Integer pictureId);
}
