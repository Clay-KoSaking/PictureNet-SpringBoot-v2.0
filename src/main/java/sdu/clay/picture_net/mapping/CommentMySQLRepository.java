package sdu.clay.picture_net.mapping;

import sdu.clay.picture_net.pojo.CommentMySQL;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface CommentMySQLRepository extends JpaRepository<CommentMySQL, String> {

    @Transactional
    @Modifying
    @Query("update CommentMySQL c set c.commentStatus = ?1 where c.mongodbId = ?2")
    int updateCommentStatus(String commentStatus, String mongodbId);

    @Transactional
    @Modifying
    @Query("update CommentMySQL c set c.commentUserId = ?1 where c.mongodbId = ?2")
    int updateCommentUserId(Integer userId, String mongodbId);

    @Transactional
    @Modifying
    @Query("update CommentMySQL c set c.commentPictureId = ?1 where c.mongodbId = ?2")
    int updateCommentPictureId(Integer pictureId, String mongodbId);

    @Transactional
    @Modifying
    @Query("update CommentMySQL c set c.commentTime = ?1 where c.mongodbId = ?2")
    int updateCommentTime(java.sql.Timestamp timestamp, String mongodbId);

//    @Transactional
//    @Modifying
//    @Query("update Comment c set c.commentContent = ?1 where c.commentId = ?2")
//    int updateCommentContent(String commentContent, Integer commentId);
}
