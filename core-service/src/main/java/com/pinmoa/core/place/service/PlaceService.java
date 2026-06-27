package com.pinmoa.core.place.service;

import com.pinmoa.core.global.exception.BusinessException;
import com.pinmoa.core.global.exception.ErrorCode;
import com.pinmoa.core.place.client.KakaoPlaceClient;
import com.pinmoa.core.place.domain.Place;
import com.pinmoa.core.place.domain.SavedPlace;
import com.pinmoa.core.place.dto.PlaceResponse;
import com.pinmoa.core.place.dto.PlaceSaveRequest;
import com.pinmoa.core.place.dto.PlaceSaverInfo;
import com.pinmoa.core.place.dto.PlaceSaversResponse;
import com.pinmoa.core.place.dto.PlaceSearchResponse;
import com.pinmoa.core.place.repository.PlaceRepository;
import com.pinmoa.core.place.repository.SavedPlaceRepository;
import com.pinmoa.core.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PlaceService {

    private final KakaoPlaceClient kakaoPlaceClient;
    private final PlaceRepository placeRepository;
    private final SavedPlaceRepository savedPlaceRepository;
    private final UserRepository userRepository;

    public List<PlaceSearchResponse> search(String query) {
        return kakaoPlaceClient.search(query).stream()
                .map(PlaceSearchResponse::from)
                .toList();
    }

    @Transactional
    public PlaceResponse savePlace(Long userId, PlaceSaveRequest request) {
        Place place = placeRepository.findByKakaoPlaceId(request.kakaoPlaceId())
                .orElseGet(() -> placeRepository.save(Place.builder()
                        .kakaoPlaceId(request.kakaoPlaceId())
                        .name(request.name())
                        .category(request.category())
                        .address(request.address())
                        .latitude(request.latitude())
                        .longitude(request.longitude())
                        .thumbnailUrl(request.thumbnailUrl())
                        .build()));

        for (Long spaceId : request.spaceIds()) {
            if (savedPlaceRepository.existsBySpaceIdAndPlaceId(spaceId, place.getId())) {
                throw new BusinessException(ErrorCode.PLACE_ALREADY_SAVED);
            }
            try {
                savedPlaceRepository.save(SavedPlace.builder()
                        .spaceId(spaceId)
                        .place(place)
                        .savedBy(userId)
                        .build());
            } catch (DataIntegrityViolationException e) {
                throw new BusinessException(ErrorCode.PLACE_ALREADY_SAVED);
            }
        }

        return PlaceResponse.from(place);
    }

    public PlaceResponse getPlace(Long placeId) {
        return PlaceResponse.from(findById(placeId));
    }

    public List<PlaceResponse> getPlacesBySpace(Long spaceId) {
        return savedPlaceRepository.findBySpaceId(spaceId).stream()
                .map(savedPlace -> PlaceResponse.from(savedPlace.getPlace()))
                .toList();
    }

    public PlaceSaversResponse getPlaceSavers(Long placeId) {
        findById(placeId);
        List<Long> saverIds = savedPlaceRepository.findSaverIdsByPlaceId(placeId);
        List<PlaceSaverInfo> savers = userRepository.findAllById(saverIds).stream()
                .map(PlaceSaverInfo::from)
                .toList();
        return new PlaceSaversResponse(savers.size(), savers);
    }

    @Transactional
    public void removePlaceFromSpace(Long placeId, Long spaceId) {
        findById(placeId);
        savedPlaceRepository.deleteBySpaceIdAndPlaceId(spaceId, placeId);
    }

    private Place findById(Long placeId) {
        return placeRepository.findById(placeId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PLACE_NOT_FOUND));
    }
}
