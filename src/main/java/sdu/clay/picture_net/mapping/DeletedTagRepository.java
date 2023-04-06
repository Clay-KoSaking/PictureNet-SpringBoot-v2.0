package sdu.clay.picture_net.mapping;

import sdu.clay.picture_net.pojo.DeletedTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface DeletedTagRepository extends JpaRepository<DeletedTag, Integer> {
    @Transactional
    @Modifying
    @Query("delete from DeletedTag deletedTag where deletedTag.tagId = ?1")
    int deleteByTagId(Integer tagId);
}
