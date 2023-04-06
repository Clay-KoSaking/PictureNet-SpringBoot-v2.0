package sdu.clay.picture_net.service;


import sdu.clay.picture_net.pojo.Follow;

public interface FollowServiceInter {

    Boolean follow(Integer followUserId, Integer followedUserId);

    Boolean unfollow(Integer followUserId, Integer followedUserId);

    Follow getFollowInfo(Integer followUserId, Integer followedUserId);
}
