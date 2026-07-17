package com.courtai.judge.repository;

import com.courtai.judge.entity.Judge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/** Repository for Judge profiles. */
@Repository
public interface JudgeRepository extends JpaRepository<Judge, Long> {

    @Query("SELECT j FROM Judge j WHERE j.user.uuid = :userUuid AND j.isDeleted = false")
    Optional<Judge> findByUserUuidAndIsDeletedFalse(@Param("userUuid") String userUuid);

    Optional<Judge> findByJudgeIdNumberAndIsDeletedFalse(String judgeIdNumber);

    boolean existsByJudgeIdNumber(String judgeIdNumber);

    Optional<Judge> findByUuidAndIsDeletedFalse(String uuid);

    Optional<Judge> findByUserUsernameAndIsDeletedFalse(String username);
}
