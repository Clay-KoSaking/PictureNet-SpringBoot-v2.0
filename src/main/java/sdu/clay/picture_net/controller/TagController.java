package sdu.clay.picture_net.controller;

import sdu.clay.picture_net.pojo.User;
import sdu.clay.picture_net.response.ComplexResponse;
import sdu.clay.picture_net.service.TagServiceInter;
import sdu.clay.picture_net.service.UserServiceInter;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping(path = "/tag")
public class TagController {
    @Autowired
    private UserServiceInter userServiceInter;
    @Autowired
    private TagServiceInter tagServiceInter;

    @PostMapping(path = "/create")
    public ComplexResponse create(@RequestParam String tagName, HttpSession httpSession) {
        synchronized (this) {
            if (httpSession.getAttribute("user") == null) {
                return new ComplexResponse(false, "Non-logged-in users cannot create tags.", null);
            } else {
                User user = userServiceInter.getUser((Integer) httpSession.getAttribute("user"));
                if (!user.getUserStatus().equals("normal")) {
                    return new ComplexResponse(false, "You cannot create tags now.", null);
                }
                if (!user.getUserType().equals("admin")) {
                    return new ComplexResponse(false, "You are not an administrator.", null);
                }
                if (tagServiceInter.createTag(tagName, user.getUserId()) == true) {
                    return new ComplexResponse(true, "Create successfully.", null);
                } else {
                    return new ComplexResponse(false, "Tag with the name already exists.", null);
                }
            }
        }
    }

    @PostMapping(path = "/delete")
    public ComplexResponse delete(@RequestParam String tagName, HttpSession httpSession) {
        synchronized (this) {
            if (httpSession.getAttribute("user") == null) {
                return new ComplexResponse(false, "Non-logged-in users cannot delete tags.", null);
            } else {
                User user = userServiceInter.getUser((Integer) httpSession.getAttribute("user"));
                if (!user.getUserStatus().equals("normal")) {
                    return new ComplexResponse(false, "You cannot delete tags now.", null);
                }
                if (!user.getUserType().equals("admin")) {
                    return new ComplexResponse(false, "You are not an administrator.", null);
                }
                if (tagServiceInter.deleteTag(tagName, user.getUserId()) == true) {
                    return new ComplexResponse(true, "Delete successfully.", null);
                } else {
                    return new ComplexResponse(false, "Cannot find the tag.", null);
                }
            }
        }
    }

    @PostMapping(path = "/modify")
    public ComplexResponse modify(@RequestParam String originalTagName, @RequestParam String tagName,
                                  HttpSession httpSession) {
        synchronized (this) {
            if (httpSession.getAttribute("user") == null) {
                return new ComplexResponse(false, "Non-logged-in users cannot modify tags.", null);
            } else {
                User user = userServiceInter.getUser((Integer) httpSession.getAttribute("user"));
                if (!user.getUserStatus().equals("normal")) {
                    return new ComplexResponse(false, "You cannot modify tags now.", null);
                }
                if (!user.getUserType().equals("admin")) {
                    return new ComplexResponse(false, "You are not an administrator.", null);
                }
                if (tagServiceInter.modifyTag(originalTagName, tagName, user.getUserId()) == true) {
                    return new ComplexResponse(true, "Modify successfully.", null);
                } else {
                    return new ComplexResponse(false, "Cannot find the tag.", null);
                }
            }
        }
    }

}
