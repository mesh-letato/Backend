package com.pinmoa.link.link.repository;

import com.pinmoa.link.link.domain.SnsLink;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LinkRepository extends JpaRepository<SnsLink, Long> {
}
