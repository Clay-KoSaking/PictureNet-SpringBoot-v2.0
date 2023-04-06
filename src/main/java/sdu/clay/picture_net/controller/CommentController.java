package sdu.clay.picture_net.controller;

import sdu.clay.picture_net.pojo.CommentMySQL;
import sdu.clay.picture_net.pojo.Picture;
import sdu.clay.picture_net.pojo.User;
import sdu.clay.picture_net.response.ComplexResponse;
import sdu.clay.picture_net.service.CommentServiceInter;
import sdu.clay.picture_net.service.PictureServiceInter;
import sdu.clay.picture_net.service.UserServiceInter;
import com.vdurmont.emoji.EmojiParser;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping(path = "/comment")
public class CommentController {
    @Autowired
    private UserServiceInter userServiceInter;
    @Autowired
    private PictureServiceInter pictureServiceInter;
    @Autowired
    private CommentServiceInter commentServiceInter;

    @PostMapping(path = "/create")
    public ComplexResponse create(@RequestParam Integer pictureId, @RequestParam String commentContent,
                                  HttpSession httpSession) {
        synchronized (this) {
            if (httpSession.getAttribute("user") == null) {
                return new ComplexResponse(false, "Non-logged-in users cannot create comments.", null);
            } else {
                User user = userServiceInter.getUser((Integer) httpSession.getAttribute("user"));
                Picture picture = pictureServiceInter.getPicture(pictureId);
                if (picture == null) {
                    return new ComplexResponse(false, "Cannot find the picture.", null);
                }
                if (!user.getUserStatus().equals("normal")) {
                    return new ComplexResponse(false, "You cannot create comments now.", null);
                }
                if (commentServiceInter.createComment(user.getUserId(), pictureId, EmojiParser.parseToAliases(commentContent)) == true) {
                    return new ComplexResponse(true, "Create successfully.", null);
                } else {
                    return new ComplexResponse(false, "Server error.", null);
                }
            }
        }
    }

    @PostMapping(path = "/delete")
    public ComplexResponse delete(@RequestParam String commentId, HttpSession httpSession) {
        synchronized (this) {
            if (httpSession.getAttribute("user") == null) {
                return new ComplexResponse(false, "Non-logged-in users cannot delete comments.", null);
            } else {
                User user = userServiceInter.getUser((Integer) httpSession.getAttribute("user"));
                CommentMySQL commentMySQL = commentServiceInter.getCommentMySQL(commentId);

                if (commentMySQL == null) {
                    return new ComplexResponse(false, "Cannot find the comment.", null);
                }
                if (!user.getUserStatus().equals("normal")) {
                    return new ComplexResponse(false, "You cannot delete comments now.", null);
                }
                if (!user.getUserType().equals("admin") && !commentMySQL.getCommentUserId().equals(user.getUserId())) {
                    return new ComplexResponse(false, "You cannot delete comments.", null);
                }
                if (commentServiceInter.deleteComment(commentId, user.getUserId()) == true) {
                    return new ComplexResponse(true, "Delete successfully.", null);
                } else {
                    return new ComplexResponse(false, "Server error.", null);
                }
            }
        }
    }
}
