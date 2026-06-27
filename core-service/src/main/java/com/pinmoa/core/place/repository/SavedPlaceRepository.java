package com.pinmoa.core.place.repository;

import com.pinmoa.core.place.domain.SavedPlace;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SavedPlaceRepository extends JpaRepository<SavedPlace, Long> {

    List<SavedPlace> findBySpaceId(Long spaceId);

    boolean existsBySpaceIdAndPlaceId(Long spaceId, Long placeId);

    void deleteBySpaceIdAndPlaceId(Long spaceId, Long placeId);
}
