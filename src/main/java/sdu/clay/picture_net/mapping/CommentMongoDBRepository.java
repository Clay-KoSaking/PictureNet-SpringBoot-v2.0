package sdu.clay.picture_net.mapping;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import sdu.clay.picture_net.pojo.CommentMongoDB;

@Repository
public interface CommentMongoDBRepository extends MongoRepository<CommentMongoDB, String> {
}
