package sdu.clay.picture_net.service;

import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import sdu.clay.picture_net.mapping.*;
import sdu.clay.picture_net.pojo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.sql.Timestamp;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

@Service
public class ResultServiceImpl implements ResultServiceInter {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PictureRepository pictureRepository;
    @Autowired
    private CheckingPictureRepository checkingPictureRepository;
    @Autowired
    private CheckingPictureTagRepository checkingPictureTagRepository;
    @Autowired
    private TagPictureRepository tagPictureRepository;
    @Autowired
    private ResultRepository resultRepository;
    private String str = "AaBbCcDdEeFfGgHhIiJjKkLlMnNnOoPpQqRrSsTtUuVvWwXxYyZz0123456789";

    @Override
    public Boolean check(Integer checkingPictureId, Integer checkerId, String checkResult) {
        CheckingPicture checkingPicture = checkingPictureRepository.findById(checkingPictureId).orElse(null);
        User user = userRepository.findById(checkerId).orElse(null);
        Timestamp timestamp = new java.sql.Timestamp(new java.util.Date().getTime());
        if (checkingPicture == null || user == null) {
            return false;
        } else {
            if (!user.getUserType().equals("checker")) {
                return false;
            }
            if (!checkResult.equals("accessed") && !checkResult.equals("failed")) {
                return false;
            } else if (checkResult.equals("accessed")) {
                if (checkingPictureRepository.findByCheckingPictureIdAndCheckingStatus(checkingPictureId, "checked") != null) {
                    return false;
                }
                Result result = new Result();
                List<Integer> checkingPictureTagList =
                        checkingPictureTagRepository.findCheckingPictureTagsByCheckingPictureId(checkingPictureId);
                Iterator it = checkingPictureTagList.iterator();

                String pictureAuthorIdStr = checkingPicture.getPictureAuthorId().toString();
                File userDirectory = new File("./image/userPictures/" + pictureAuthorIdStr);
                if (!userDirectory.exists()) {
                    userDirectory.mkdirs();
                }

                File source = new File(checkingPicture.getPicturePath());
                String sourceName = source.getName();
                String destName = "./image/userPictures/" + pictureAuthorIdStr + "/" + sourceName;
                File dest = new File(destName);

                try {
                    FileChannel input = new FileInputStream(source).getChannel();
                    FileChannel output = new FileOutputStream(dest).getChannel();
                    output.transferFrom(input, 0, input.size());
                    input.close();
                    output.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (checkingPicture.getOriginalPictureId().equals(0)) {
                    Picture picture = new Picture();
                    picture.setPictureAuthorId(checkingPicture.getPictureAuthorId());
                    picture.setPictureName(checkingPicture.getPictureName());
                    picture.setPicturePath(destName);
                    picture.setPictureIntro(checkingPicture.getPictureIntro());
                    picture.setPictureView(0);
                    picture.setPictureTransmit(0);
                    picture.setPictureModifyTime(timestamp);
                    picture.setPictureStatus("normal");
                    pictureRepository.save(picture);

                    Integer cnt = pictureRepository.findAll().size();
                    saveTagsOfPicture(it, cnt);

                    result.setPictureId(cnt);
                } else {
                    Integer originalId = checkingPicture.getOriginalPictureId();
                    pictureRepository.updatePictureAuthorId(checkingPicture.getPictureAuthorId(), originalId);
                    pictureRepository.updatePictureName(checkingPicture.getPictureName(), originalId);
                    pictureRepository.updatePicturePath(destName, originalId);
                    pictureRepository.updatePictureIntro(checkingPicture.getPictureIntro(), originalId);

                    if (pictureRepository.findById(originalId).orElse(null).getPictureStatus().equals("deleted")) {
                        pictureRepository.updatePictureView(0, originalId);
                        pictureRepository.updatePictureTransmit(0, originalId);
                    }

                    pictureRepository.updatePictureModifyTime(timestamp, originalId);
                    pictureRepository.updatePictureStatus("normal", originalId);
                    saveTagsOfPicture(it, originalId);

                    result.setPictureId(originalId);
                }
                checkingPictureTagRepository.deleteCheckingPictureTagByCheckingPictureId(checkingPictureId);
                checkingPictureRepository.updateCheckingStatus("checked", checkingPictureId);

                result.setCheckerId(checkerId);
                result.setCheckTime(timestamp);
                result.setCheckResult("accessed");
                resultRepository.save(result);

                return true;
            } else {
                if (checkingPicture.getOriginalPictureId().equals(0)) {
                    checkingPictureRepository.updateCheckingStatus("checked", checkingPictureId);
                    return false;
                } else {
                    Result result = new Result();
                    result.setPictureId(checkingPicture.getOriginalPictureId());
                    result.setCheckerId(checkerId);
                    result.setCheckTime(timestamp);
                    result.setCheckResult("failed");
                    resultRepository.save(result);

                    checkingPictureRepository.updateCheckingStatus("checked", checkingPictureId);
                    return true;
                }
            }
        }
    }

    public void saveTagsOfPicture(Iterator it, Integer saveWhere) {
        while (it.hasNext()) {
            Integer tagId = (Integer) it.next();
            TagPicture tagPicture = new TagPicture();
            tagPicture.setTagId(tagId);
            tagPicture.setPictureId(saveWhere);
            tagPictureRepository.save(tagPicture);
        }
    }
}
