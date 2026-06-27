package com.pinmoa.core.place.service;

import com.pinmoa.core.global.exception.BusinessException;
import com.pinmoa.core.global.exception.ErrorCode;
import com.pinmoa.core.place.client.KakaoPlaceClient;
import com.pinmoa.core.place.dto.PlaceResponse;
import com.pinmoa.core.place.dto.PlaceSaveRequest;
import com.pinmoa.core.place.dto.PlaceSearchResponse;
import com.pinmoa.core.place.entity.Place;
import com.pinmoa.core.place.entity.SavedPlace;
import com.pinmoa.core.place.repository.PlaceRepository;
import com.pinmoa.core.place.repository.SavedPlaceRepository;
import com.pinmoa.core.space.entity.Space;
import com.pinmoa.core.space.repository.SpaceRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PlaceService {

    private final KakaoPlaceClient kakaoPlaceClient;
    private final PlaceRepository placeRepository;
    private final SavedPlaceRepository savedPlaceRepository;
    private final SpaceRepository spaceRepository;

    public List<PlaceSearchResponse> search(String query) {
        return kakaoPlaceClient.search(query).stream()
            .map(PlaceSearchResponse::from)
            .toList();
    }

    @Transactional
    public PlaceResponse save(PlaceSaveRequest request, Long userId) {
        Space space = spaceRepository.findById(request.spaceId())
            .orElseThrow(() -> new BusinessException(ErrorCode.SPACE_NOT_FOUND));

        Place place = placeRepository.findByKakaoPlaceId(request.kakaoPlaceId())
            .orElseGet(() -> placeRepository.save(Place.builder()
                .kakaoPlaceId(request.kakaoPlaceId())
                .name(request.name())
                .category(request.category())
                .address(request.address())
                .latitude(request.latitude())
                .longitude(request.longitude())
                .build()));

        if (savedPlaceRepository.existsBySpaceIdAndPlaceId(space.getId(), place.getId())) {
            throw new BusinessException(ErrorCode.PLACE_ALREADY_SAVED);
        }

        savedPlaceRepository.save(SavedPlace.builder()
            .space(space)
            .place(place)
            .savedBy(userId)
            .build());

        return PlaceResponse.from(place);
    }

    @Transactional(readOnly = true)
    public List<PlaceResponse> getSavedPlaces(Long spaceId) {
        return savedPlaceRepository.findBySpaceId(spaceId).stream()
            .map(savedPlace -> PlaceResponse.from(savedPlace.getPlace()))
            .toList();
    }
}
