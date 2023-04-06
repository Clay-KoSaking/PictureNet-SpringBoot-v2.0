package sdu.clay.picture_net.mapping;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import sdu.clay.picture_net.pojo.MessageMongoDB;

@Repository
public interface MessageMongoDBRepository extends MongoRepository<MessageMongoDB, String> {

}
