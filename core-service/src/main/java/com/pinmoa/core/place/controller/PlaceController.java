package com.pinmoa.core.place.controller;

import com.pinmoa.core.place.dto.PlaceResponse;
import com.pinmoa.core.place.dto.PlaceSaveRequest;
import com.pinmoa.core.place.dto.PlaceSearchResponse;
import com.pinmoa.core.place.service.PlaceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

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

    @Operation(summary = "장소를 스페이스에 저장")
    @PostMapping
    public ResponseEntity<PlaceResponse> save(
        @RequestBody @Valid PlaceSaveRequest request,
        @AuthenticationPrincipal Long userId
    ) {
        return ResponseEntity.ok(placeService.save(request, userId));
    }

    @Operation(summary = "스페이스에 저장된 장소 목록 조회")
    @GetMapping
    public ResponseEntity<List<PlaceResponse>> getSavedPlaces(@RequestParam Long spaceId) {
        return ResponseEntity.ok(placeService.getSavedPlaces(spaceId));
    }
}
