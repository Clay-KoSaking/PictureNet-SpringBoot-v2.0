package sdu.clay.picture_net.pojo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "deletedcomment")
public class DeletedComment {
    @Id
    @Column(name = "mongodb_id", nullable = false)
    private String mongoId;

    public String getMongoId() {
        return mongoId;
    }

    public void setMongoId(String mongoId) {
        this.mongoId = mongoId;
    }
}
