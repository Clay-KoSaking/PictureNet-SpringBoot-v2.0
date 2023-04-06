package sdu.clay.picture_net.service;

public interface MessageServiceInter {

    Boolean sendMessage(Integer sendUserId, Integer receiveUserId, String messageContent);
}
