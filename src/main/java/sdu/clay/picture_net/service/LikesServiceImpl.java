package sdu.clay.picture_net.service;

import sdu.clay.picture_net.mapping.LikesRepository;
import sdu.clay.picture_net.mapping.PictureRepository;
import sdu.clay.picture_net.mapping.UserRepository;
import sdu.clay.picture_net.pojo.Likes;
import sdu.clay.picture_net.pojo.Picture;
import sdu.clay.picture_net.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;

@Service
public class LikesServiceImpl implements LikesServiceInter {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PictureRepository pictureRepository;
    @Autowired
    private LikesRepository likesRepository;
    @Override
    public Boolean createLikes(Integer userId, Integer pictureId) {
        User user = userRepository.findById(userId).orElse(null);
        Picture picture = pictureRepository.findById(pictureId).orElse(null);
        if (user == null || picture == null) {
            return false;
        } else {
            Timestamp timestamp = new java.sql.Timestamp(new java.util.Date().getTime());
            Likes foundLikes = likesRepository.findLikesByUserIdAndPictureId(userId, pictureId);
            if (foundLikes == null) {
                Likes likes = new Likes();
                likes.setUserId(userId);
                likes.setPictureId(pictureId);
                likes.setLikeTime(timestamp);
                likes.setIsLiking(1);
                likesRepository.save(likes);
            } else {
                if (foundLikes.getIsLiking().equals(1)) {
                    return false;
                }
                likesRepository.updateLikeTime(timestamp, userId, pictureId);
                likesRepository.updateIsLiking(1, userId, pictureId);
            }
            return true;
        }
    }

    @Override
    public Boolean cancelLikes(Integer userId, Integer pictureId) {
        User user = userRepository.findById(userId).orElse(null);
        Picture picture = pictureRepository.findById(pictureId).orElse(null);
        if (user == null || picture == null) {
            return false;
        } else {
            Likes foundLikes = likesRepository.findLikesByUserIdAndPictureId(userId, pictureId);
            if (foundLikes == null) {
                return false;
            }
            if (foundLikes.getIsLiking().equals(0)) {
                return false;
            }
            likesRepository.updateIsLiking(0, userId, pictureId);
            return true;
        }
    }

    @Override
    public Integer countByPictureId(Integer pictureId) {
        return likesRepository.countByPictureId(pictureId);
    }

}
