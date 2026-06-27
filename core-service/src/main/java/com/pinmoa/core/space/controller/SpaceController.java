package com.pinmoa.core.space.controller;

import com.pinmoa.core.global.response.ApiResponse;
import com.pinmoa.core.space.dto.SpaceCreateRequest;
import com.pinmoa.core.space.dto.SpaceResponse;
import com.pinmoa.core.space.dto.SpaceUpdateRequest;
import com.pinmoa.core.space.service.SpaceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Space", description = "스페이스 관련 API")
@RestController
@RequestMapping("/api/core/spaces")
@RequiredArgsConstructor
public class SpaceController {

    private final SpaceService spaceService;

    @Operation(summary = "스페이스 생성")
    @PostMapping
    public ResponseEntity<ApiResponse<SpaceResponse>> createSpace(
            @RequestHeader("X-USER-ID") Long userId,
            @Valid @RequestBody SpaceCreateRequest request
    ) {
        SpaceResponse response = spaceService.createSpace(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "스페이스 생성 성공"));
    }

    @Operation(summary = "내 스페이스 목록 조회")
    @GetMapping
    public ResponseEntity<ApiResponse<List<SpaceResponse>>> getMySpaces(
            @RequestHeader("X-USER-ID") Long userId
    ) {
        List<SpaceResponse> responses = spaceService.getMySpaces(userId);
        return ResponseEntity.ok(ApiResponse.success(responses, "스페이스 목록 조회 성공"));
    }

    @Operation(summary = "스페이스 단건 조회")
    @GetMapping("/{spaceId}")
    public ResponseEntity<ApiResponse<SpaceResponse>> getSpaceById(
            @PathVariable Long spaceId,
            @RequestHeader("X-USER-ID") Long userId
    ) {
        SpaceResponse response = spaceService.getSpaceById(spaceId, userId);
        return ResponseEntity.ok(ApiResponse.success(response, "스페이스 조회 성공"));
    }

    @Operation(summary = "스페이스 수정 (OWNER만 가능)")
    @PatchMapping("/{spaceId}")
    public ResponseEntity<ApiResponse<SpaceResponse>> updateSpace(
            @PathVariable Long spaceId,
            @RequestHeader("X-USER-ID") Long userId,
            @RequestBody SpaceUpdateRequest request
    ) {
        SpaceResponse response = spaceService.updateSpace(spaceId, userId, request);
        return ResponseEntity.ok(ApiResponse.success(response, "스페이스 수정 성공"));
    }

    @Operation(summary = "스페이스 삭제 (OWNER만 가능)")
    @DeleteMapping("/{spaceId}")
    public ResponseEntity<ApiResponse<Void>> deleteSpace(
            @PathVariable Long spaceId,
            @RequestHeader("X-USER-ID") Long userId
    ) {
        spaceService.deleteSpace(spaceId, userId);
        return ResponseEntity.ok(ApiResponse.success(null, "스페이스 삭제 성공"));
    }
}
