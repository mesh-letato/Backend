package com.pinmoa.core.place.repository;

import com.pinmoa.core.place.entity.SavedPlace;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SavedPlaceRepository extends JpaRepository<SavedPlace, Long> {

    List<SavedPlace> findBySpaceId(Long spaceId);

    boolean existsBySpaceIdAndPlaceId(Long spaceId, Long placeId);
}
