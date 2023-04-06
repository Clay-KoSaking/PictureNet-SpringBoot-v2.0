package sdu.clay.picture_net.service;

import sdu.clay.picture_net.mapping.*;
import sdu.clay.picture_net.pojo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;

@Service
public class CommentServiceImpl implements CommentServiceInter {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PictureRepository pictureRepository;
    @Autowired
    private CommentMySQLRepository commentMySQLRepository;
    @Autowired
    private CommentMongoDBRepository commentMongoDBRepository;
    @Autowired
    private DeletedCommentRepository deletedCommentRepository;
    @Override
    public Boolean createComment(Integer userId, Integer pictureId, String commentContent) {
        User user = userRepository.findById(userId).orElse(null);
        Picture picture = pictureRepository.findById(pictureId).orElse(null);
        if (user == null || picture == null) {
            return false;
        }
        if (!user.getUserStatus().equals("normal")) {
            return false;
        }
        List<DeletedComment> deletedCommentList = deletedCommentRepository.findAll();
        Timestamp timestamp = new java.sql.Timestamp(new java.util.Date().getTime());
        if (deletedCommentList.isEmpty()) {
//            Comment comment = new Comment();
//            comment.setCommentUserId(userId);
//            comment.setCommentPictureId(pictureId);
//            comment.setCommentContent(commentContent);
//            comment.setCommentTime(timestamp);
//            comment.setCommentStatus("normal");
//            commentRepository.save(comment);
            CommentMongoDB commentMongoDB = new CommentMongoDB();
            CommentMySQL commentMySQL = new CommentMySQL();

            commentMongoDB.setCommentContent(commentContent);
            CommentMongoDB tmp = commentMongoDBRepository.save(commentMongoDB);

            commentMySQL.setMongodbId(tmp.getId());
            commentMySQL.setCommentUserId(userId);
            commentMySQL.setCommentPictureId(pictureId);
            commentMySQL.setCommentTime(timestamp);
            commentMySQL.setCommentStatus("normal");
            commentMySQLRepository.save(commentMySQL);
        } else {
            String foundId = deletedCommentList.get(0).getMongoId();
            commentMySQLRepository.updateCommentUserId(userId, foundId);
            commentMySQLRepository.updateCommentPictureId(pictureId, foundId);
            commentMySQLRepository.updateCommentTime(timestamp, foundId);
//            commentRepository.updateCommentContent(commentContent, foundId);
            commentMySQLRepository.updateCommentStatus("normal", foundId);

            CommentMongoDB foundComment = commentMongoDBRepository.findById(foundId).get();
            foundComment.setCommentContent(commentContent);
            commentMongoDBRepository.save(foundComment);
            deletedCommentRepository.deleteByCommentId(foundId);
        }
        return true;
    }

    @Override
    public Boolean deleteComment(String commentId, Integer userId) {
        CommentMySQL commentMySQL = commentMySQLRepository.findById(commentId).orElse(null);
        if (commentMySQL == null) {
            return false;
        } else {
            User user = userRepository.findById(userId).orElse(null);
            if (user == null) {
                return false;
            }
            if (!user.getUserStatus().equals("normal")) {
                return false;
            }
            if (!user.getUserType().equals("admin") && !commentMySQL.getCommentUserId().equals(userId)) {
                return false;
            } else {
                commentMySQLRepository.updateCommentStatus("deleted", commentId);
                DeletedComment deletedComment = new DeletedComment();
                deletedComment.setMongoId(commentId);
                deletedCommentRepository.save(deletedComment);
                return true;
            }
        }
    }

    @Override
    public CommentMySQL getCommentMySQL(String commentId) {
        return commentMySQLRepository.findById(commentId).orElse(null);
    }

    @Override
    public CommentMongoDB getCommentMongoDB(String mongoId) {
        return commentMongoDBRepository.findById(mongoId).get();
    }
}
