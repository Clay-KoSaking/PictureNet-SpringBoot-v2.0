package sdu.clay.picture_net.pojo;

import jakarta.persistence.*;

@Entity
@Table(name = "comment")
public class CommentMySQL {
    @Id
    @Column(name = "mongodb_id", nullable = false, length = 50)
    private String mongodbId;
    @Column(name = "comment_user_id", nullable = false)
    private Integer commentUserId;
    @Column(name = "comment_picture_id", nullable = false)
    private Integer commentPictureId;
    @Column(name = "comment_time", nullable = false)
    private java.sql.Timestamp commentTime;
    @Column(name = "comment_status", nullable = false)
    private String commentStatus;


    public String getMongodbId() {
        return mongodbId;
    }

    public void setMongodbId(String mongodbId) {
        this.mongodbId = mongodbId;
    }

    public Integer getCommentUserId() {
      return commentUserId;
    }

    public void setCommentUserId(Integer commentUserId) {
      this.commentUserId = commentUserId;
    }


    public Integer getCommentPictureId() {
      return commentPictureId;
    }

    public void setCommentPictureId(Integer commentPictureId) {
      this.commentPictureId = commentPictureId;
    }


    public java.sql.Timestamp getCommentTime() {
      return commentTime;
    }

    public void setCommentTime(java.sql.Timestamp commentTime) {
      this.commentTime = commentTime;
    }

    public String getCommentStatus() {
        return commentStatus;
    }

    public void setCommentStatus(String commentStatus) {
        this.commentStatus = commentStatus;
    }
}
