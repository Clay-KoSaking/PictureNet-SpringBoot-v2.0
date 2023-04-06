package sdu.clay.picture_net.controller;

import com.vdurmont.emoji.EmojiParser;
import sdu.clay.picture_net.information.UserInfo;
import sdu.clay.picture_net.pojo.User;
import sdu.clay.picture_net.response.ComplexResponse;
import sdu.clay.picture_net.service.*;
import sdu.clay.picture_net.tool.ImageUtils;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@CrossOrigin
@RequestMapping(path = "/user")
public class UserController {

    @Autowired
    private UserServiceInter userServiceInter;
    @Autowired
    private UserServiceImpl userService;
    @Autowired
    private FollowServiceInter followServiceInter;
    @Autowired
    private MessageServiceInter messageServiceInter;
    @Autowired
    private PictureServiceInter pictureServiceInter;

    public UserController(UserServiceInter userServiceInter) {
        this.userServiceInter = userServiceInter;
    }

    @PostMapping(path = "/login")
    public ComplexResponse login(@RequestParam String userName, @RequestParam String userPassword, HttpSession httpSession) {
        synchronized (this) {
            if (httpSession.getAttribute("user") != null) {
                return new ComplexResponse(false, "You've been already logged in.", null);
            } else {
                User user = userServiceInter.getUser(userName);
                if (user == null) {
                    return new ComplexResponse(false, "Wrong username.", null);
                } else {
                    if (!user.getUserPassword().equals(User.getHashedPassword(userPassword))) {
                        return new ComplexResponse(false, "Wrong password.", null);
                    }
                    if (user.getUserStatus().equals("deleted")) {
                        return new ComplexResponse(false, "Cannot find the user.", null);
                    } else {
                        httpSession.setAttribute("user", user.getUserId());
                        return new ComplexResponse(true, "Login successfully.", null);
                    }
                }
            }
        }
    }

    @GetMapping(path = "/logout")
    public ComplexResponse logout(HttpSession httpSession) {
        synchronized (this) {
            if (httpSession.getAttribute("user") != null) {
                httpSession.removeAttribute("user");
                return new ComplexResponse(true, "Logout successfully.", null);
            } else {
                return new ComplexResponse(false, "You have not been logged in yet.", null);
            }
        }
    }

    @GetMapping(path = "/getInfo/{userId}")
    public ComplexResponse getInfo(@PathVariable("userId") Integer userId) {
        synchronized (this) {
            User user = userServiceInter.getUser(userId);
            if (user == null) {
                return new ComplexResponse(false, "Cannot find the user.", null);
            } else {
                if (user.getUserStatus().equals("deleted")) {
                    return new ComplexResponse(false, "Cannot find the user.", null);
                }
                if (userId.equals(1)) {
                    return new ComplexResponse(false, "Invalid operation.", null);
                }
                return new ComplexResponse(true, "", new UserInfo(
                        user.getUserId(), user.getUserName(), user.getUserIntro(), user.getUserEmail(),
                        user.getUserType(), user.getUserStatus(), user.getUserImage(),
                        pictureServiceInter.findPicturesByPictureAuthorId(user.getUserId())
                ));
            }
        }
    }

    @PostMapping(path = "/register")
    public ComplexResponse register(@RequestParam String userName, @RequestParam String userPassword,
                                    @RequestParam(required = false) String userIntro, @RequestParam String userEmail,
                                    @RequestParam(required = false) MultipartFile userImage) throws Exception {
        synchronized (this) {
            if (userImage != null) {
                InputStream inputStream = userImage.getInputStream();
                BufferedImage bufferedImage = ImageIO.read(inputStream);
                if (bufferedImage == null) {
                    return new ComplexResponse(false, "The file type uploaded is not an image.", null);
                } else {
                    userServiceInter.uploadUserImage(userImage, inputStream, bufferedImage);
                }
            }

            Integer registerId;
            if ((registerId = userServiceInter.createUser(userName, userPassword,
                    EmojiParser.parseToAliases(userIntro == null ? "The user doesn't have an intro now." : userIntro),
                    userEmail, userImage)) != -1) {
                messageServiceInter.sendMessage(1, registerId,
                        "Welcome to PictureNet! Let's enjoy the fun of creation and sharing here~~~");
                return new ComplexResponse(true, "Register successfully.", null);
            } else {
                String regex = "^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$";
                Pattern p = Pattern.compile(regex);
                Matcher m = p.matcher(userEmail);
                if (userServiceInter.getUser(userName) != null &&
                        !userServiceInter.getUser(userName).getUserStatus().equals("deleted")) {
                    return new ComplexResponse(false, "The username has been used.", null);
                }
                if (userName.length() > 20) {
                    return new ComplexResponse(false, "The username is too long.", null);
                }
                if (userPassword.length() < 8 || userPassword.length() > 32) {
                    return new ComplexResponse(false, "The password must consists of 8 to 32 characters.", null);
                }
                if (!m.matches()) {
                    return new ComplexResponse(false, "The email format is illegal.", null);
                }
                if (userEmail.length() > 30) {
                    return new ComplexResponse(false, "The email is too long.", null);
                }
                if (userIntro != null && userIntro.length() > 50) {
                    return new ComplexResponse(false, "The intro is too long.", null);
                }
                return new ComplexResponse(false, "Server error.", null);
            }
        }
    }

    @PostMapping(path = "/status")
    public ComplexResponse changeStatus(@RequestParam Integer userId,
                                        @RequestParam String userStatus,
                                        @RequestParam(required = false) String bannedReason,
                                        HttpSession httpSession) {
        synchronized (this) {
            if (httpSession.getAttribute("user") == null) {
                return new ComplexResponse(false, "Non-logged-in users cannot delete accounts", null);
            } else {
                User user = userServiceInter.getUser((Integer) httpSession.getAttribute("user"));
                if (!user.getUserType().equals("admin")) {
                    return new ComplexResponse(false, "You cannot control other's accounts", null);
                } else {
                    User foundUser = userServiceInter.getUser(userId);
                    if (userId.equals(1)) {
                        return new ComplexResponse(false, "Invalid operation.", null);
                    }
                    if (foundUser == null) {
                        return new ComplexResponse(false, "Cannot find the user.", null);
                    } else if (foundUser.getUserStatus().equals("deleted")) {
                        return new ComplexResponse(false, "Cannot find the user.", null);
                    } else {
                        if (userStatus.equals("deleted")) {
                            if (user.getUserId().equals(userId)) {
                                return new ComplexResponse(false, "Server error.", null);
                            } else {
                                userServiceInter.deleteUser(userId);
                                return new ComplexResponse(true, "Delete successfully.", null);
                            }
                        } else if (userStatus.equals("banned")) {
                            if (foundUser.getUserStatus().equals("banned") || user.getUserId().equals(userId)) {
                                return new ComplexResponse(false, "Server error.", null);
                            } else {
                                userServiceInter.banUser(userId, bannedReason);
                                return new ComplexResponse(true, "Ban successfully.", null);
                            }
                        } else if (userStatus.equals("normal")) {
                            userServiceInter.updateUserStatus("normal", userId);
                            userServiceInter.updateIsBanned(0, 1, userId);
                            return new ComplexResponse(true, "Return normal successfully.", null);
                        } else {
                            return new ComplexResponse(false, "Wrong type", null);
                        }
                    }
                }
            }
        }
    }

    @PostMapping(path = "/modify")
    public ComplexResponse modify(@RequestParam(required = false) String userName,
                                  @RequestParam(required = false) String userPassword,
                                  @RequestParam(required = false) String userEmail,
                                  @RequestParam(required = false) MultipartFile userImage,
                                  HttpSession httpSession) throws Exception {
        synchronized (this) {
            if (httpSession.getAttribute("user") == null) {
                return new ComplexResponse(false, "Non-logged-in users cannot modify other's information", null);
            } else {
                User user = userServiceInter.getUser((Integer) httpSession.getAttribute("user"));
                if (userName != null) {
                    if (userServiceInter.getUser(userName) != null && !userServiceInter.getUser(userName).equals(user)) {
                        return new ComplexResponse(false, "The user with this name exists.", null);
                    }
                    if (userName.length() > 20) {
                        return new ComplexResponse(false, "The username is too long.", null);
                    }
                    userServiceInter.updateUserName(userName, user.getUserId());
                }
                if (userPassword != null) {
                    if (userPassword.length() < 8 || userPassword.length() > 32) {
                        return new ComplexResponse(false, "The password must consists of 8 to 32 characters.", null);
                    } else {
                        userServiceInter.updateUserPassword(User.getHashedPassword(userPassword), user.getUserId());
                    }
                }
                if (userEmail != null) {
                    String regex = "^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$";
                    Pattern p = Pattern.compile(regex);
                    Matcher m = p.matcher(userEmail);
                    if (!m.matches()) {
                        return new ComplexResponse(false, "The email format is illegal.", null);
                    }
                    if (userEmail.length() > 30) {
                        return new ComplexResponse(false, "The email is too long.", null);
                    }
                    userServiceInter.updateUserEmail(userEmail, user.getUserId());
                }
                if (userImage != null) {
                    InputStream inputStream = userImage.getInputStream();
                    BufferedImage bufferedImage = ImageIO.read(inputStream);
                    inputStream.close();
                    if (bufferedImage == null) {
                        return new ComplexResponse(false, "The file type uploaded is not an image.", null);
                    } else {
                        userServiceInter.uploadUserImage(userImage, inputStream, bufferedImage);
                        String userImagePath = user.getUserImage();
                        File originImage = new File(userImagePath);
                        originImage.delete();
                        File newImage = new File(new File(userImagePath).getAbsolutePath());
                        userImage.transferTo(newImage);
                        userServiceInter.updateUserImage(userImagePath, user.getUserId());
                    }
                }
                return new ComplexResponse(true, "Modify successfully.", null);
            }
        }
    }

    @PostMapping(path = "/grant")
    public ComplexResponse grant(@RequestParam Integer userId, @RequestParam String userType, HttpSession httpSession) {
        synchronized (this) {
            if (httpSession.getAttribute("user") == null) {
                return new ComplexResponse(false, "Non-logged-in users don't have privilege to grant.", null);
            } else {
                User user = userServiceInter.getUser((Integer) httpSession.getAttribute("user"));
                if (userId.equals(1)) {
                    return new ComplexResponse(false, "Invalid operation.", null);
                }
                if (!user.getUserType().equals("admin")) {
                    return new ComplexResponse(false, "You don't have privilege to grant.", null);
                } else {
                    if (userServiceInter.getUser(userId) == null) {
                        return new ComplexResponse(false, "The user who will be granted doesn't exist.", null);
                    }
                    if (!userType.equals("common") && !userType.equals("checker") && !userType.equals("admin")) {
                        return new ComplexResponse(false, "Wrong type.", null);
                    }
                    userServiceInter.updateUserType(userType, userId);
                    return new ComplexResponse(true, "Grant successfully.", null);
                }
            }
        }
    }

    @PostMapping(path = "/follow")
    public ComplexResponse follow(@RequestParam Integer followedUserId, HttpSession httpSession) {
        synchronized (this) {
            if (httpSession.getAttribute("user") == null) {
                return new ComplexResponse(false, "Non-logged-in users cannot follow others.", null);
            } else {
                User user = userServiceInter.getUser((Integer) httpSession.getAttribute("user"));
                if (followedUserId.equals(1)) {
                    return new ComplexResponse(false, "Invalid operation.", null);
                }
                if (user.getUserStatus().equals("banned")) {
                    return new ComplexResponse(false, "You cannot follow others now.", null);
                } else {
                    if (userServiceInter.getUser(followedUserId) == null) {
                        return new ComplexResponse(false, "Cannot find the user.", null);
                    }
                    if (followServiceInter.getFollowInfo(user.getUserId(), followedUserId) != null) {
                        return new ComplexResponse(false, "You have been already followed.", null);
                    }
                    followServiceInter.follow(user.getUserId(), followedUserId);
                    return new ComplexResponse(true, "Follow successfully.", null);
                }
            }
        }
    }

    @PostMapping(path = "/unfollow")
    public ComplexResponse unfollow(@RequestParam Integer followedUserId, HttpSession httpSession) {
        synchronized (this) {
            if (httpSession.getAttribute("user") == null) {
                return new ComplexResponse(false, "Non-logged-in users cannot unfollow.", null);
            } else {
                User user = userServiceInter.getUser((Integer) httpSession.getAttribute("user"));
                if (followedUserId.equals(1)) {
                    return new ComplexResponse(false, "Invalid operation.", null);
                }
                if (user.getUserStatus().equals("banned")) {
                    return new ComplexResponse(false, "You cannot unfollow now.", null);
                } else {
                    if (userServiceInter.getUser(followedUserId) == null) {
                        return new ComplexResponse(false, "Cannot find the user.", null);
                    }
                    if (followServiceInter.getFollowInfo(user.getUserId(), followedUserId) == null) {
                        return new ComplexResponse(false, "You have not followed the user yet.", null);
                    }
                    followServiceInter.unfollow(user.getUserId(), followedUserId);
                    return new ComplexResponse(true, "Unfollow successfully.", null);
                }
            }
        }
    }

    @PostMapping(path = "/send")
    public ComplexResponse send(@RequestParam Integer receiveUserId,
                                @RequestParam String messageContent, HttpSession httpSession) {
        synchronized (this) {
            if (httpSession.getAttribute("user") == null) {
                return new ComplexResponse(false, "Non-logged-in users cannot send messages to others", null);
            } else {
                User user = userServiceInter.getUser((Integer) httpSession.getAttribute("user"));
                if (receiveUserId.equals(1)) {
                    return new ComplexResponse(false, "Invalid operation.", null);
                }
                if (!user.getUserStatus().equals("normal")) {
                    return new ComplexResponse(false, "You cannot send messages.", null);
                }
                if (userServiceInter.getUser(receiveUserId) == null) {
                    return new ComplexResponse(false, "Cannot find the receiver.", null);
                } else {
                    messageServiceInter.sendMessage(user.getUserId(), receiveUserId, messageContent);
                    return new ComplexResponse(true, "The message has been sent successfully.", null);
                }
            }
        }
    }
}
