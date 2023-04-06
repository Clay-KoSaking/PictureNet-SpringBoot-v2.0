package sdu.clay.picture_net.service;

import sdu.clay.picture_net.pojo.CommentMongoDB;
import sdu.clay.picture_net.pojo.CommentMySQL;

public interface CommentServiceInter {
    Boolean createComment(Integer userId, Integer pictureId, String commentContent);

    Boolean deleteComment(String commentId, Integer userId);

    CommentMySQL getCommentMySQL(String commentId);

    CommentMongoDB getCommentMongoDB(String mongoId);
}
