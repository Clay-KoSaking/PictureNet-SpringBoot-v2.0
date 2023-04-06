package sdu.clay.picture_net.information;

import java.util.List;

public class UserInfo {

    private Integer userId;
    private String userName;
    private String userIntro;
    private String userEmail;
    private String userType;
    private String userStatus;
    private String userImage;
    private List<Integer> pictureIds;


    public long getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }


    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserIntro() {
        return userIntro;
    }

    public void setUserIntro(String userIntro) {
        this.userIntro = userIntro;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }


    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(String userStatus) {
        this.userStatus = userStatus;
    }

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }

    public List<Integer> getPictureIds() {
        return pictureIds;
    }

    public void setPictureIds(List<Integer> pictureIds) {
        this.pictureIds = pictureIds;
    }

    public UserInfo(Integer userId, String userName, String userIntro, String userEmail, String userType,
                    String userStatus, String userImage, List<Integer> pictureIds) {
        this.userId = userId;
        this.userName = userName;
        this.userIntro = userIntro;
        this.userEmail = userEmail;
        this.userType = userType;
        this.userStatus = userStatus;
        this.userImage = userImage;
        this.pictureIds = pictureIds;
    }
}
