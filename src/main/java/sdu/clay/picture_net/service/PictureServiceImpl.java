package sdu.clay.picture_net.service;

import sdu.clay.picture_net.mapping.*;
import sdu.clay.picture_net.pojo.*;
import sdu.clay.picture_net.response.ComplexResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import java.sql.Timestamp;
import java.util.*;

@Service
public class PictureServiceImpl implements PictureServiceInter {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PictureRepository pictureRepository;
    @Autowired
    private DeletedPictureRepository deletedPictureRepository;
    @Autowired
    private MessageMongoDBRepository messageMongoDBRepository;
    @Autowired
    private MessageMySQLRepository messageMySQLRepository;
    @Autowired
    private TagRepository tagRepository;
    @Autowired
    private TagPictureRepository tagPictureRepository;
    @Autowired
    private CheckingPictureRepository checkingPictureRepository;
    @Autowired
    private CheckingPictureTagRepository checkingPictureTagRepository;
    @Autowired
    private ResultRepository resultRepository;
    @Autowired
    private MessageServiceInter messageServiceInter;
    private String str = "AaBbCcDdEeFfGgHhIiJjKkLlMnNnOoPpQqRrSsTtUuVvWwXxYyZz0123456789";
    private Random random;
    private StringBuffer stringBuffer;
    private File file;
    private String picturePath;

    @Override
    public Picture getPicture(Integer pictureId) {
        return pictureRepository.findById(pictureId).orElse(null);
    }

    @Override
    public Boolean createPicture(Integer pictureAuthorId, String pictureName,
                                 MultipartFile pictureContent, String pictureIntro, Set<String> tagNameSet) {
        try {
            /* Require: pictureAuthorId, pictureName and pictureContent are not null. */
            if (pictureName == null || pictureContent == null) {
                return false;
            }
            /* Determine if the uploaded file is an image. */
            InputStream inputStream = pictureContent.getInputStream();
            BufferedImage bufferedImage = ImageIO.read(inputStream);
            if (bufferedImage == null) {
                return false;
            }
            /* Non-existent users and banned users cannot create images. */
            User user = userRepository.findById(pictureAuthorId).orElse(null);
            if (user == null) {
                return false;
            } else {
                if (!user.getUserStatus().equals("normal")) {
                    return false;
                } else {
                    List<DeletedPicture> deletedPictureList = deletedPictureRepository.findAll();

                    if (tagNameSet != null) {
                        if (!tagNameSet.isEmpty()) {
                            for (String tagName : tagNameSet) {
                                Tag foundTag = tagRepository.findTagByTagName(tagName);
                                if (foundTag == null) {
                                    return false;
                                }
                            }
                        }
                    }

                    String pictureAuthorIdStr = pictureAuthorId.toString();
                    File userDirectory = new File("./image/checkingPictures/" + pictureAuthorIdStr);
                    if (!userDirectory.exists()) {
                        userDirectory.mkdirs();
                    }

                    generatePathAndSavePicture(pictureAuthorIdStr, pictureContent);

                    CheckingPicture checkingPicture = new CheckingPicture();
                    checkingPicture.setPictureAuthorId(pictureAuthorId);
                    checkingPicture.setPictureName(pictureName);
                    checkingPicture.setPicturePath(picturePath);
                    checkingPicture.setPictureIntro(pictureIntro);
                    checkingPicture.setCheckingStatus("checking");
                    if (deletedPictureList.isEmpty()) {
                        checkingPicture.setOriginalPictureId(0);
                    } else {
                        Integer foundId = deletedPictureList.get(0).getPictureId();
                        checkingPicture.setOriginalPictureId(foundId);
                        deletedPictureRepository.deleteByPictureId(foundId);
                    }
                    checkingPictureRepository.save(checkingPicture);

                    Integer cnt = checkingPictureRepository.findAll().size();

                    if (tagNameSet != null) {
                        Iterator it = tagNameSet.iterator();
                        traversalCheckingPictureListAndSaveTags(cnt, it);
                    }

                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Integer> findPicturesByPictureAuthorId(Integer pictureAuthorId) {
        return pictureRepository.findPicturesByPictureAuthorId(pictureAuthorId);
    }

    @Override
    public void updatePictureViewPlusOne(Integer pictureId) {
        pictureRepository.updatePictureViewPlusOne(pictureId);
    }

    @Override
    public void updatePictureTransmitPlusOne(Integer pictureId) {
        pictureRepository.updatePictureTransmitPlusOne(pictureId);
    }

    @Override
    public Boolean transmitPicture(Integer pictureId, Integer sendUserId, Integer receiveUserId) {
        Picture picture = pictureRepository.findById(pictureId).orElse(null);
        User sendUser = userRepository.findById(sendUserId).orElse(null);
        User receiveUser = userRepository.findById(receiveUserId).orElse(null);
        if (picture == null || sendUser == null || receiveUser == null) {
            return false;
        } else {
            if (!sendUser.getUserStatus().equals("normal")) {
                return false;
            }
            String content = "I really love this picture! Let's have a look ~~~ pictureId: " + pictureId;

            messageServiceInter.sendMessage(sendUserId, receiveUserId, content);

            pictureRepository.updatePictureTransmitPlusOne(pictureId);
            return true;
        }
    }

    @Override
    public Boolean deletePicture(Integer pictureId, Integer userId) {
        Picture picture = pictureRepository.findById(pictureId).orElse(null);
        User user = userRepository.findById(userId).orElse(null);
        if (picture == null || user == null) {
            return false;
        }
        if (picture.getPictureStatus().equals("deleted")) {
            return false;
        }
        if (!picture.getPictureAuthorId().equals(userId) &&
                !(user.getUserType().equals("admin") && user.getUserStatus().equals("normal"))) {
            return false;
        } else {
            pictureRepository.updatePictureStatus("deleted", pictureId);
            DeletedPicture deletedPicture = new DeletedPicture();
            deletedPicture.setPictureId(pictureId);
            deletedPictureRepository.save(deletedPicture);
            tagPictureRepository.deleteByPictureId(pictureId);
            resultRepository.deleteByPictureId(pictureId);
            return true;
        }
    }

    @Override
    public Boolean modifyPicture(Integer pictureId, Integer userId, String pictureName,
                                 MultipartFile pictureContent, String pictureIntro, Set<String> tagNameSet) {
        try {
            Picture picture = pictureRepository.findById(pictureId).orElse(null);
            User user = userRepository.findById(userId).orElse(null);
            if (picture == null || user == null) {
                return false;
            }
            if (!picture.getPictureAuthorId().equals(user.getUserId())) {
                return false;
            }

            Iterator it = tagNameSet.iterator();
            if (!tagNameSet.isEmpty()) {
                for (String tagName : tagNameSet) {
                    Tag foundTag = tagRepository.findTagByTagName(tagName);
                    if (foundTag == null) {
                        return false;
                    }
                }
            }

            String userIdStr = userId.toString();
            generatePathAndSavePicture(userIdStr, pictureContent);

            CheckingPicture checkingPicture = new CheckingPicture();
            checkingPicture.setPictureAuthorId(userId);
            checkingPicture.setPictureName(pictureName);
            checkingPicture.setPicturePath(picturePath);
            checkingPicture.setPictureIntro(pictureIntro);
            checkingPicture.setOriginalPictureId(pictureId);
            checkingPicture.setCheckingStatus("checking");
            checkingPictureRepository.save(checkingPicture);

            tagPictureRepository.deleteByPictureId(pictureId);

            Integer cnt = checkingPictureRepository.findAll().size();
            traversalCheckingPictureListAndSaveTags(cnt, it);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public List<Integer> findTagsByPictureId(Integer pictureId) {
        return tagPictureRepository.findByPictureId(pictureId);
    }

    @Override
    public List<Picture> searchByKeyword(String keyword) {
        return pictureRepository.searchByKeyword(keyword);
    }


    /* Encapsulate some heavily used code as methods. */
    public void generatePathAndSavePicture(String pictureAuthorIdStr, MultipartFile pictureContent) throws Exception {
        do {
            random = new Random();
            stringBuffer = new StringBuffer();
            for (int i = 0; i < 15; i++) {
                int num = random.nextInt(62);
                stringBuffer.append(str.charAt(num));
            }
            String fileName = stringBuffer.toString();
            picturePath = "./image/checkingPictures/" + pictureAuthorIdStr + "/" + fileName + ".png";
            file = new File(picturePath);
        } while (file.exists());
        File absoluteFile = new File(file.getAbsolutePath());
        pictureContent.transferTo(absoluteFile);
    }

    public void traversalCheckingPictureListAndSaveTags(Integer cnt, Iterator it) {
        while (it.hasNext()) {
            String tagName = (String) it.next();
            Tag foundTag = tagRepository.findTagByTagName(tagName);
            CheckingPictureTag checkingPictureTag = new CheckingPictureTag();
            checkingPictureTag.setCheckingPictureId(cnt);
            checkingPictureTag.setCheckingPictureTagId(foundTag.getTagId());
            checkingPictureTagRepository.save(checkingPictureTag);
        }
    }
}
