package sdu.clay.picture_net.pojo;

import jakarta.persistence.*;

@Entity
@Table(name = "message")
public class MessageMySQL {
    @Id
    @Column(name = "mongodb_id", nullable = false, length = 50)
    private String mongodbId;
    @Column(name = "send_user_id", nullable = false)
    private Integer sendUserId;
    @Column(name = "receive_user_id", nullable = false)
    private Integer receiveUserId;
    @Column(name = "send_time", nullable = false)
    private java.sql.Timestamp sendTime;

    public MessageMySQL() {
    }

    public String getMongodbId() {
        return mongodbId;
    }

    public void setMongodbId(String mongodbId) {
        this.mongodbId = mongodbId;
    }

    public Integer getSendUserId() {
        return sendUserId;
    }

    public void setSendUserId(Integer sendUserId) {
        this.sendUserId = sendUserId;
    }


    public Integer getReceiveUserId() {
        return receiveUserId;
    }

    public void setReceiveUserId(Integer receiveUserId) {
        this.receiveUserId = receiveUserId;
    }


    public java.sql.Timestamp getSendTime() {
        return sendTime;
    }

    public void setSendTime(java.sql.Timestamp sendTime) {
        this.sendTime = sendTime;
    }

}