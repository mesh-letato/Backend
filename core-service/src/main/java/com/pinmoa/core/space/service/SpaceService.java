package com.pinmoa.core.space.service;

import com.pinmoa.core.global.exception.BusinessException;
import com.pinmoa.core.global.exception.ErrorCode;
import com.pinmoa.core.space.dto.SpaceCreateRequest;
import com.pinmoa.core.space.dto.SpaceResponse;
import com.pinmoa.core.space.dto.SpaceUpdateRequest;
import com.pinmoa.core.space.entity.Space;
import com.pinmoa.core.space.entity.SpaceMember;
import com.pinmoa.core.space.entity.SpaceRole;
import com.pinmoa.core.space.entity.SpaceType;
import com.pinmoa.core.space.repository.SpaceMemberRepository;
import com.pinmoa.core.space.repository.SpaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SpaceService {

    private final SpaceRepository spaceRepository;
    private final SpaceMemberRepository spaceMemberRepository;

    @Transactional
    public SpaceResponse createSpace(Long userId, SpaceCreateRequest request) {
        String inviteCode = request.type() == SpaceType.SHARED
                ? UUID.randomUUID().toString().replace("-", "").substring(0, 10)
                : null;

        Space space = Space.builder()
                .ownerId(userId)
                .name(request.name())
                .emoji(request.emoji())
                .type(request.type())
                .inviteCode(inviteCode)
                .build();
        spaceRepository.save(space);

        SpaceMember owner = SpaceMember.builder()
                .space(space)
                .userId(userId)
                .role(SpaceRole.OWNER)
                .build();
        spaceMemberRepository.save(owner);

        return SpaceResponse.from(space);
    }

    public List<SpaceResponse> getMySpaces(Long userId) {
        return spaceMemberRepository.findSpacesByUserId(userId).stream()
                .map(SpaceResponse::from)
                .toList();
    }

    public SpaceResponse getSpaceById(Long spaceId, Long userId) {
        Space space = spaceRepository.findById(spaceId)
                .orElseThrow(() -> new BusinessException(ErrorCode.SPACE_NOT_FOUND));

        if (!spaceMemberRepository.existsBySpaceIdAndUserId(spaceId, userId)) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }

        return SpaceResponse.from(space);
    }

    @Transactional
    public SpaceResponse updateSpace(Long spaceId, Long userId, SpaceUpdateRequest request) {
        Space space = spaceRepository.findById(spaceId)
                .orElseThrow(() -> new BusinessException(ErrorCode.SPACE_NOT_FOUND));

        SpaceMember member = spaceMemberRepository.findBySpaceIdAndUserId(spaceId, userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.UNAUTHORIZED));

        if (member.getRole() != SpaceRole.OWNER) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }

        space.update(request.name(), request.emoji());
        return SpaceResponse.from(space);
    }

    @Transactional
    public void deleteSpace(Long spaceId, Long userId) {
        Space space = spaceRepository.findById(spaceId)
                .orElseThrow(() -> new BusinessException(ErrorCode.SPACE_NOT_FOUND));

        SpaceMember member = spaceMemberRepository.findBySpaceIdAndUserId(spaceId, userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.UNAUTHORIZED));

        if (member.getRole() != SpaceRole.OWNER) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }

        spaceRepository.delete(space);
    }
}