package sdu.clay.picture_net.controller;

import sdu.clay.picture_net.information.PictureMicroInfo;
import sdu.clay.picture_net.information.SearchInfo;
import sdu.clay.picture_net.information.UserMicroInfo;
import sdu.clay.picture_net.pojo.Picture;
import sdu.clay.picture_net.pojo.User;
import sdu.clay.picture_net.response.ComplexResponse;
import sdu.clay.picture_net.service.PictureServiceImpl;
import sdu.clay.picture_net.service.UserServiceInter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
@CrossOrigin
@RequestMapping(path = "/search")
public class SearchController {
    @Autowired
    private UserServiceInter userServiceInter;
    @Autowired
    private PictureServiceImpl pictureService;
    @GetMapping(path = "/{keyword}")
    public ComplexResponse search(@PathVariable("keyword") String keyword) {
        synchronized (this) {
            List<User> gotUserList = userServiceInter.searchByKeyword(keyword);
            List<Picture> gotPictureList = pictureService.searchByKeyword(keyword);

            Set<UserMicroInfo> userMicroInfoSet = new HashSet<>();
            Set<PictureMicroInfo> pictureMicroInfoSet = new HashSet<>();

            for (User user : gotUserList) {
                Integer userId = user.getUserId();
                String userName = user.getUserName();
                UserMicroInfo userMicroInfo = new UserMicroInfo();
                userMicroInfo.setUserId(userId);
                userMicroInfo.setUserName(userName);
                userMicroInfoSet.add(userMicroInfo);
            }
            for (Picture picture : gotPictureList) {
                Integer pictureId = picture.getPictureId();
                Integer pictureAuthorId = picture.getPictureAuthorId();
                String pictureName = picture.getPictureName();
                PictureMicroInfo pictureMicroInfo = new PictureMicroInfo();
                pictureMicroInfo.setPictureId(pictureId);
                pictureMicroInfo.setPictureAuthorId(pictureAuthorId);
                pictureMicroInfo.setPictureName(pictureName);
                pictureMicroInfoSet.add(pictureMicroInfo);
            }

            return new ComplexResponse(true, "Search successfully.", new SearchInfo(
                    userMicroInfoSet, pictureMicroInfoSet
            ));
        }
    }
}
