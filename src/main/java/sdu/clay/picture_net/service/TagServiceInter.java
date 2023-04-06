package sdu.clay.picture_net.service;

import sdu.clay.picture_net.pojo.Tag;

import java.util.List;

public interface TagServiceInter {
    Boolean createTag(String tagName, Integer createUserId);

    Boolean deleteTag(String tagName, Integer deleteUserId);

    Boolean modifyTag(String originalTagName, String tagName, Integer modifyUserId);

    Tag findTagByTagName(String tagName);

    String findTagNameByTagId(Integer tagId);

    List<Integer> findByTagId(Integer tagId);
}
