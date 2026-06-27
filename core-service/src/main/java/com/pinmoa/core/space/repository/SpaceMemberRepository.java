package com.pinmoa.core.space.repository;

import com.pinmoa.core.space.entity.Space;
import com.pinmoa.core.space.entity.SpaceMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SpaceMemberRepository extends JpaRepository<SpaceMember, Long> {

    @Query("SELECT sm.space FROM SpaceMember sm WHERE sm.userId = :userId")
    List<Space> findSpacesByUserId(@Param("userId") Long userId);

    Optional<SpaceMember> findBySpaceIdAndUserId(Long spaceId, Long userId);

    boolean existsBySpaceIdAndUserId(Long spaceId, Long userId);
}
