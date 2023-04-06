package sdu.clay.picture_net.mapping;

import sdu.clay.picture_net.pojo.Result;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface ResultRepository extends JpaRepository<Result, Integer> {
    @Transactional
    @Modifying
    @Query("delete from Result r where r.pictureId = ?1")
    int deleteByPictureId(Integer pictureId);

    @Query(value = "select * from result where picture_id = ?1 and check_result = ?2", nativeQuery = true)
    Result findByPictureIdAndCheckResult(Integer pictureId, String checkResult);
}
