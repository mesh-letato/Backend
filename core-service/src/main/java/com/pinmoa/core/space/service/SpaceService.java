package com.pinmoa.core.space.service;

import com.pinmoa.core.global.exception.BusinessException;
import com.pinmoa.core.global.exception.ErrorCode;
import com.pinmoa.core.place.repository.SavedPlaceRepository;
import com.pinmoa.core.space.dto.SpaceCreateRequest;
import com.pinmoa.core.space.dto.SpaceMemberResponse;
import com.pinmoa.core.space.dto.SpaceResponse;
import com.pinmoa.core.space.dto.SpaceUpdateRequest;
import com.pinmoa.core.space.entity.Space;
import com.pinmoa.core.space.entity.SpaceMember;
import com.pinmoa.core.space.entity.SpaceRole;
import com.pinmoa.core.space.entity.SpaceType;
import com.pinmoa.core.space.repository.SpaceMemberRepository;
import com.pinmoa.core.space.repository.SpaceRepository;
import com.pinmoa.core.user.domain.User;
import com.pinmoa.core.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SpaceService {

    private final SpaceRepository spaceRepository;
    private final SpaceMemberRepository spaceMemberRepository;
    private final SavedPlaceRepository savedPlaceRepository;
    private final UserRepository userRepository;

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

        return SpaceResponse.from(space, 1, 0);
    }

    public List<SpaceResponse> getMySpaces(Long userId) {
        return spaceMemberRepository.findSpacesByUserId(userId).stream()
                .map(space -> SpaceResponse.from(space,
                        spaceMemberRepository.countBySpaceId(space.getId()),
                        savedPlaceRepository.countBySpaceId(space.getId())))
                .toList();
    }

    public SpaceResponse getSpaceById(Long spaceId, Long userId) {
        Space space = spaceRepository.findById(spaceId)
                .orElseThrow(() -> new BusinessException(ErrorCode.SPACE_NOT_FOUND));

        if (!spaceMemberRepository.existsBySpaceIdAndUserId(spaceId, userId)) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }

        return SpaceResponse.from(space,
                spaceMemberRepository.countBySpaceId(spaceId),
                savedPlaceRepository.countBySpaceId(spaceId));
    }

    public List<SpaceMemberResponse> getSpaceMembers(Long spaceId, Long userId) {
        if (!spaceRepository.existsById(spaceId)) {
            throw new BusinessException(ErrorCode.SPACE_NOT_FOUND);
        }
        if (!spaceMemberRepository.existsBySpaceIdAndUserId(spaceId, userId)) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }

        List<SpaceMember> members = spaceMemberRepository.findAllBySpaceId(spaceId);
        List<Long> userIds = members.stream().map(SpaceMember::getUserId).toList();
        Map<Long, User> userMap = userRepository.findAllById(userIds).stream()
                .collect(Collectors.toMap(User::getId, u -> u));

        return members.stream()
                .filter(m -> userMap.containsKey(m.getUserId()))
                .map(m -> SpaceMemberResponse.of(m, userMap.get(m.getUserId())))
                .toList();
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
        return SpaceResponse.from(space,
                spaceMemberRepository.countBySpaceId(spaceId),
                savedPlaceRepository.countBySpaceId(spaceId));
    }

    @Transactional
    public SpaceResponse joinSpace(Long userId, String inviteCode) {
        Space space = spaceRepository.findByInviteCode(inviteCode)
                .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_INVITE_CODE));

        if (spaceMemberRepository.existsBySpaceIdAndUserId(space.getId(), userId)) {
            throw new BusinessException(ErrorCode.ALREADY_SPACE_MEMBER);
        }

        spaceMemberRepository.save(SpaceMember.builder()
                .space(space)
                .userId(userId)
                .role(SpaceRole.MEMBER)
                .build());

        return SpaceResponse.from(space,
                spaceMemberRepository.countBySpaceId(space.getId()),
                savedPlaceRepository.countBySpaceId(space.getId()));
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
