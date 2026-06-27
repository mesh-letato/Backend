package com.pinmoa.core.place.controller;

import com.pinmoa.core.place.dto.PlaceResponse;
import com.pinmoa.core.place.dto.PlaceSaveRequest;
import com.pinmoa.core.place.dto.PlaceSearchResponse;
import com.pinmoa.core.place.service.PlaceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Place", description = "장소 관련 API")
@RestController
@RequestMapping("/api/core/places")
@RequiredArgsConstructor
public class PlaceController {

    private final PlaceService placeService;

    @Operation(summary = "카카오맵 장소 검색")
    @GetMapping("/search")
    public ResponseEntity<List<PlaceSearchResponse>> search(@RequestParam String query) {
        return ResponseEntity.ok(placeService.search(query));
    }

    @Operation(summary = "장소 저장 (카카오맵 검색 결과 → 스페이스에 저장)")
    @PostMapping
    public ResponseEntity<PlaceResponse> savePlace(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody PlaceSaveRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(placeService.savePlace(userId, request));
    }

    @Operation(summary = "장소 단건 조회")
    @GetMapping("/{placeId}")
    public ResponseEntity<PlaceResponse> getPlace(@PathVariable Long placeId) {
        return ResponseEntity.ok(placeService.getPlace(placeId));
    }

    @Operation(summary = "스페이스 내 장소 목록 조회")
    @GetMapping
    public ResponseEntity<List<PlaceResponse>> getPlacesBySpace(@RequestParam Long spaceId) {
        return ResponseEntity.ok(placeService.getPlacesBySpace(spaceId));
    }

    @Operation(summary = "스페이스에서 장소 제거")
    @DeleteMapping("/{placeId}/spaces/{spaceId}")
    public ResponseEntity<Void> removePlaceFromSpace(
            @PathVariable Long placeId,
            @PathVariable Long spaceId
    ) {
        placeService.removePlaceFromSpace(placeId, spaceId);
        return ResponseEntity.noContent().build();
    }
}
