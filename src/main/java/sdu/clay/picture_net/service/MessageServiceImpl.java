package sdu.clay.picture_net.service;

import sdu.clay.picture_net.mapping.MessageMongoDBRepository;
import sdu.clay.picture_net.mapping.MessageMySQLRepository;
import sdu.clay.picture_net.mapping.UserRepository;
import sdu.clay.picture_net.pojo.MessageMongoDB;
import sdu.clay.picture_net.pojo.MessageMySQL;
import sdu.clay.picture_net.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;

@Service
public class MessageServiceImpl implements MessageServiceInter {

    @Autowired
    private MessageMongoDBRepository messageMongoDBRepository;
    @Autowired
    private MessageMySQLRepository messageMySQLRepository;
    @Autowired
    private UserRepository userRepository;

    @Override
    public Boolean sendMessage(Integer sendUserId, Integer receiveUserId, String messageContent) {
        User sendUser = userRepository.findById(sendUserId).orElse(null);
        User receiveUser = userRepository.findById(receiveUserId).orElse(null);
        if (sendUser == null || receiveUser == null) {
            return false;
        } else {
            MessageMongoDB messageMongoDB = new MessageMongoDB();
            MessageMySQL messageMySQL = new MessageMySQL();
            Timestamp timestamp = new java.sql.Timestamp(new java.util.Date().getTime());

            messageMongoDB.setMessageContent(messageContent);
            MessageMongoDB tmp = messageMongoDBRepository.save(messageMongoDB);

            messageMySQL.setMongodbId(tmp.getId());
            messageMySQL.setSendUserId(sendUserId);
            messageMySQL.setReceiveUserId(receiveUserId);
            messageMySQL.setSendTime(timestamp);
            messageMySQLRepository.save(messageMySQL);
            return true;
        }
    }
}
