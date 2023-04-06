package sdu.clay.picture_net.service;

import sdu.clay.picture_net.mapping.CheckingPictureRepository;
import sdu.clay.picture_net.pojo.CheckingPicture;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CheckingPictureServiceImpl implements CheckingPictureServiceInter {
    @Autowired
    private CheckingPictureRepository checkingPictureRepository;

    @Override
    public CheckingPicture getCheckingPictureByCheckingPictureId(Integer checkingPictureId) {
        return checkingPictureRepository.findById(checkingPictureId).orElse(null);
    }

    @Override
    public CheckingPicture findByCheckingPictureIdAndCheckingStatus(Integer checkingPictureId, String checkingStatus) {
        return checkingPictureRepository.findByCheckingPictureIdAndCheckingStatus(checkingPictureId, checkingStatus);
    }
}
