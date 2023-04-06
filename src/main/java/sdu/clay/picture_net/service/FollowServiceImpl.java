package sdu.clay.picture_net.service;

import sdu.clay.picture_net.mapping.FollowRepository;
import sdu.clay.picture_net.mapping.UserRepository;
import sdu.clay.picture_net.pojo.Follow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import sdu.clay.picture_net.pojo.User;

import java.sql.Timestamp;

@Service
public class FollowServiceImpl implements FollowServiceInter {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private FollowRepository followRepository;

    @Override
    public Boolean follow(Integer followUserId, Integer followedUserId) {
        User followUser = userRepository.findById(followUserId).orElse(null);
        User followedUser = userRepository.findById(followedUserId).orElse(null);
        if (followUser == null || followedUser == null) {
            return false;
        } else {
            if (followRepository.findFollowByFollowUserIdAndFollowedUserId(followUserId, followedUserId) != null) {
                return false;
            }
            Follow follow = new Follow();
            Timestamp timestamp = new java.sql.Timestamp(new java.util.Date().getTime());
            follow.setFollowUserId(followUserId);
            follow.setFollowedUserId(followedUserId);
            follow.setFollowTime(timestamp);
            followRepository.save(follow);
            return true;
        }
    }

    @Override
    public Boolean unfollow(Integer followUserId, Integer followedUserId) {
        User followUser = userRepository.findById(followUserId).orElse(null);
        User followedUser = userRepository.findById(followedUserId).orElse(null);
        if (followUser == null || followedUser == null) {
            return false;
        }
        if (followRepository.findFollowByFollowUserIdAndFollowedUserId(followUserId, followedUserId) == null) {
            return false;
        } else {
            followRepository.deleteByFollowUserIdAndFollowedUserId(followUserId, followedUserId);
            return true;
        }
    }

    @Override
    public Follow getFollowInfo(Integer followUserId, Integer followedUserId) {
        return followRepository.findFollowByFollowUserIdAndFollowedUserId(followUserId, followedUserId);
    }
}
