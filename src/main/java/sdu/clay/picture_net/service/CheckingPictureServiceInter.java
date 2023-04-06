package sdu.clay.picture_net.service;

import sdu.clay.picture_net.pojo.CheckingPicture;

public interface CheckingPictureServiceInter {
    CheckingPicture getCheckingPictureByCheckingPictureId(Integer checkingPictureId);

    CheckingPicture findByCheckingPictureIdAndCheckingStatus(Integer checkingPictureId, String checkingStatus);
}
