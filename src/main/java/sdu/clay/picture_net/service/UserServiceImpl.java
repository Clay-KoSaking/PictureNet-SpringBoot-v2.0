package sdu.clay.picture_net.service;

import sdu.clay.picture_net.mapping.BannedUserRepository;
import sdu.clay.picture_net.mapping.DeletedUserRepository;
import sdu.clay.picture_net.mapping.UserRepository;
import sdu.clay.picture_net.pojo.BannedUser;
import sdu.clay.picture_net.pojo.DeletedUser;
import sdu.clay.picture_net.pojo.User;
import sdu.clay.picture_net.tool.ImageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Timestamp;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class UserServiceImpl implements UserServiceInter {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private DeletedUserRepository deletedUserRepository;
    @Autowired
    private BannedUserRepository bannedUserRepository;
    private String str = "AaBbCcDdEeFfGgHhIiJjKkLlMnNnOoPpQqRrSsTtUuVvWwXxYyZz0123456789";
    private Random random;
    private StringBuffer stringBuffer;
    private String userImagePath;
    private Integer registerId;

    @Override
    public User getUser(Integer userId) {
        return userRepository.findById(userId).orElse(null);
    }

    @Override
    public User getUser(String userName) {
        User user = userRepository.findUserByUserName(userName);
        return user;
    }

    @Override
    public User getUser(String userName, String userPassword) {
        return userRepository.findUserByUserNameAndUserPassword(userName, userPassword);
    }

    @Override
    public Integer createUser(String userName, String userPassword, String userIntro,
                              String userEmail, MultipartFile userImage) {
        try {
            String regex = "^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$";
            Pattern p = Pattern.compile(regex);
            Matcher m = p.matcher(userEmail);
            if (!m.matches() ||
                    (userRepository.findUserByUserName(userName) != null &&
                     !userRepository.findUserByUserName(userName).getUserStatus().equals("deleted")) ||
                    userPassword.length() < 8 || userPassword.length() > 32 || userIntro.length() > 50) {
                return -1;
            } else {
                if (userImage != null) {
                    random = new Random();
                    stringBuffer = new StringBuffer();
                    for (int i = 0; i < 10; i++) {
                        int num = random.nextInt(62);
                        stringBuffer.append(str.charAt(num));
                    }
                    String fileName = stringBuffer.toString();
                    userImagePath = "./image/userImage/" + fileName + ".png";
                    File file = new File(userImagePath);
                    File absoluteFile = new File(file.getAbsolutePath());
                    userImage.transferTo(absoluteFile);
                } else {
                    userImagePath = "./image/default.png";
                }

                List<DeletedUser> deletedUserList = deletedUserRepository.findAll();
                if (deletedUserList.isEmpty()) {
                    User user = new User();
                    user.setUserName(userName);
                    user.setRawPassword(userPassword);
                    user.setUserIntro(userIntro);
                    user.setUserEmail(userEmail);
                    user.setUserType("common");
                    user.setUserStatus("normal");
                    user.setUserImage(userImagePath);
                    userRepository.save(user);
                    registerId = userRepository.findAll().size();
                } else {
                    Integer foundId = deletedUserList.get(0).getUserId();
                    userRepository.updateUserName(userName, foundId);
                    userRepository.updateUserPassword(userPassword, foundId);
                    userRepository.updateUserIntro(userIntro, foundId);
                    userRepository.updateUserEmail(userEmail, foundId);
                    userRepository.updateUserType("common", foundId);
                    userRepository.updateUserStatus("normal", foundId);
                    userRepository.updateUserImage(userImagePath, foundId);
                    deletedUserRepository.deleteByUserId(foundId);
                    registerId = foundId;
                }
                return registerId;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    public Boolean deleteUser(Integer userId) {
        try {
            User user = userRepository.findById(userId).orElse(null);
            if (user == null) {
                return false;
            }
            if (user.getUserStatus().equals("deleted")) {
                return false;
            } else {
                userRepository.updateUserStatus("deleted", user.getUserId());
                DeletedUser deletedUser = new DeletedUser();
                deletedUser.setUserId(user.getUserId());
                deletedUserRepository.save(deletedUser);
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public Boolean banUser(Integer userId, String bannedReason) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return false;
        } else {
            if (user.getUserStatus().equals("normal")) {
                BannedUser bannedUser = new BannedUser();
                Timestamp timestamp = new java.sql.Timestamp(new java.util.Date().getTime());
                bannedUser.setUserId(userId);
                bannedUser.setBannedTime(timestamp);
                bannedUser.setBannedReason(bannedReason);
                bannedUser.setIsBanned(1);
                bannedUserRepository.save(bannedUser);
                userRepository.updateUserStatus("banned", userId);
                return true;
            } else {
                return false;
            }
        }
    }

    @Override
    public void updateUserName(String userName, Integer userId) {
        userRepository.updateUserName(userName, userId);
    }

    @Override
    public void updateUserPassword(String userPassword, Integer userId) {
        userRepository.updateUserPassword(userPassword, userId);
    }

    @Override
    public void updateUserEmail(String userEmail, Integer userId) {
        userRepository.updateUserEmail(userEmail, userId);
    }

    @Override
    public void updateUserType(String userType, Integer userId) {
        userRepository.updateUserType(userType, userId);
    }

    @Override
    public void updateUserStatus(String userStatus, Integer userId) {
        userRepository.updateUserStatus(userStatus, userId);
    }

    @Override
    public void updateUserImage(String userImage, Integer userId) {
        userRepository.updateUserImage(userImage, userId);
    }

    @Override
    public void updateIsBanned(Integer isBanned, Integer originalIsBanned, Integer userId) {
        bannedUserRepository.updateIsBanned(isBanned, originalIsBanned, userId);
    }

    @Override
    public void uploadUserImage(MultipartFile userImage, InputStream inputStream, BufferedImage bufferedImage) {
        try {
            BufferedImage newImage = ImageUtils.resizeImage(bufferedImage, 512, 512);

            random = new Random();
            stringBuffer = new StringBuffer();
            for (int i = 0; i < 8; i++) {
                int num = random.nextInt(62);
                stringBuffer.append(str.charAt(num));
            }
            String tmpFilePath = "./image/tmpImage/" + stringBuffer.toString() + ".png";
            File file = new File(tmpFilePath);
            ImageIO.write(newImage, "png", file);

            FileInputStream fileInputStream = new FileInputStream(tmpFilePath);
            BufferedImage bufferedImage1 = ImageIO.read(fileInputStream);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage1, "png", byteArrayOutputStream);
            userImage = new MockMultipartFile(tmpFilePath, byteArrayOutputStream.toByteArray());

            byteArrayOutputStream.close();
            fileInputStream.close();
            inputStream.close();

            file.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<User> searchByKeyword(String keyword) {
        return userRepository.searchByKeyword(keyword);
    }
}
