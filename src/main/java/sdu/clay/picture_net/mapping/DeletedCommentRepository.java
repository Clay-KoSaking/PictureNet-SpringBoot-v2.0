package sdu.clay.picture_net.mapping;

import sdu.clay.picture_net.pojo.DeletedComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface DeletedCommentRepository extends JpaRepository<DeletedComment, String> {
    @Transactional
    @Modifying
    @Query("delete from DeletedComment dc where dc.mongoId = ?1")
    int deleteByCommentId(String mongoId);
}
