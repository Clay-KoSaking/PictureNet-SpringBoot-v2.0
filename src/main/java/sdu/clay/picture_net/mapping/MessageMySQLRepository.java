package sdu.clay.picture_net.mapping;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sdu.clay.picture_net.pojo.MessageMySQL;

@Repository
public interface MessageMySQLRepository extends JpaRepository<MessageMySQL, String> {
}
