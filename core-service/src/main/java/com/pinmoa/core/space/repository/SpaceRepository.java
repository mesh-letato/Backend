package com.pinmoa.core.space.repository;

import com.pinmoa.core.space.entity.Space;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SpaceRepository extends JpaRepository<Space, Long> {

    Optional<Space> findByInviteCode(String inviteCode);
}
