package sdu.clay.picture_net.service;

public interface LikesServiceInter {
    Boolean createLikes(Integer userId, Integer pictureId);

    Boolean cancelLikes(Integer userId, Integer pictureId);

    Integer countByPictureId(Integer pictureId);
}
