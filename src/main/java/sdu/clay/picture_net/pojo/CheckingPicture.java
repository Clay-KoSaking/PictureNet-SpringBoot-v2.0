package sdu.clay.picture_net.pojo;

import jakarta.persistence.*;

@Entity
@Table(name = "checkingpicture")
public class CheckingPicture {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "checking_picture_id", nullable = false)
    private Integer checkingPictureId;
    @Column(name = "picture_author_id", nullable = false)
    private Integer pictureAuthorId;
    @Column(name = "picture_name", nullable = false)
    private String pictureName;
    @Column(name = "picture_path", nullable = false)
    private String picturePath;
    @Column(name = "picture_intro", nullable = false)
    private String pictureIntro;
    @Column(name = "original_picture_id", nullable = false)
    private Integer originalPictureId;
    @Column(name = "checking_status", nullable = false)
    private String checkingStatus;
  
  
    public Integer getCheckingPictureId() {
      return checkingPictureId;
    }
  
    public void setCheckingPictureId(Integer checkingPictureId) {
      this.checkingPictureId = checkingPictureId;
    }
  
  
    public Integer getPictureAuthorId() {
      return pictureAuthorId;
    }
  
    public void setPictureAuthorId(Integer pictureAuthorId) {
      this.pictureAuthorId = pictureAuthorId;
    }
  
  
    public String getPictureName() {
      return pictureName;
    }
  
    public void setPictureName(String pictureName) {
      this.pictureName = pictureName;
    }
  
  
    public String getPicturePath() {
      return picturePath;
    }
  
    public void setPicturePath(String picturePath) {
      this.picturePath = picturePath;
    }
  
  
    public String getPictureIntro() {
      return pictureIntro;
    }
  
    public void setPictureIntro(String pictureIntro) {
    this.pictureIntro = pictureIntro;
  }

    public Integer getOriginalPictureId() {
        return originalPictureId;
    }

    public void setOriginalPictureId(Integer originalPictureId) {
        this.originalPictureId = originalPictureId;
    }

    public String getCheckingStatus() {
        return checkingStatus;
    }

    public void setCheckingStatus(String checkingStatus) {
        this.checkingStatus = checkingStatus;
    }
}
