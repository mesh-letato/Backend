package com.pinmoa.core.place.repository;

import com.pinmoa.core.place.domain.SavedPlace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SavedPlaceRepository extends JpaRepository<SavedPlace, Long> {

    List<SavedPlace> findBySpaceId(Long spaceId);

    boolean existsBySpaceIdAndPlaceId(Long spaceId, Long placeId);

    void deleteBySpaceIdAndPlaceId(Long spaceId, Long placeId);

    long countBySpaceId(Long spaceId);

    @Query("SELECT DISTINCT sp.savedBy FROM SavedPlace sp WHERE sp.place.id = :placeId")
    List<Long> findSaverIdsByPlaceId(@Param("placeId") Long placeId);
}
