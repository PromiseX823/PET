package com.example.app.service;

import com.example.app.dto.response.FollowResponse;
import com.example.app.entity.Follow;
import com.example.app.entity.User;
import com.example.app.exception.BusinessException;
import com.example.app.repository.FollowRepository;
import com.example.app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FollowService {

    private final FollowRepository followRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    /**
     * 关注用户
     */
    @Transactional
    public FollowResponse followUser(Long followerId, Long followingId) {
        if (followerId.equals(followingId)) {
            throw new BusinessException("不能关注自己");
        }

        User follower = userRepository.findById(followerId)
                .orElseThrow(() -> new BusinessException("关注者不存在"));
        User following = userRepository.findById(followingId)
                .orElseThrow(() -> new BusinessException("被关注者不存在"));

        if (followRepository.existsByFollowerIdAndFollowingId(followerId, followingId)) {
            throw new BusinessException("已经关注过该用户");
        }

        Follow follow = Follow.builder()
                .followerId(followerId)
                .followingId(followingId)
                .build();
        follow = followRepository.save(follow);

        // 发送通知
        try {
            notificationService.createNotification(
                    followingId,
                    "新增粉丝",
                    follower.getUsername() + " 关注了你",
                    "follow",
                    followerId
            );
        } catch (Exception e) {
            // 通知发送失败不影响关注
        }

        return toFollowResponse(follow, follower, following);
    }

    /**
     * 取消关注
     */
    @Transactional
    public void unfollowUser(Long followerId, Long followingId) {
        if (!followRepository.existsByFollowerIdAndFollowingId(followerId, followingId)) {
            throw new BusinessException("未关注该用户");
        }
        followRepository.deleteByFollowerIdAndFollowingId(followerId, followingId);
    }

    /**
     * 切换关注状态
     */
    @Transactional
    public Map<String, Object> toggleFollow(Long followerId, Long followingId) {
        Map<String, Object> result = new HashMap<>();
        if (followRepository.existsByFollowerIdAndFollowingId(followerId, followingId)) {
            unfollowUser(followerId, followingId);
            result.put("following", false);
        } else {
            followUser(followerId, followingId);
            result.put("following", true);
        }
        result.put("followerCount", getFollowerCount(followingId));
        result.put("followingCount", getFollowingCount(followerId));
        return result;
    }

    /**
     * 检查是否已关注
     */
    @Transactional(readOnly = true)
    public boolean isFollowing(Long followerId, Long followingId) {
        return followRepository.existsByFollowerIdAndFollowingId(followerId, followingId);
    }

    /**
     * 获取关注数
     */
    @Transactional(readOnly = true)
    public int getFollowingCount(Long userId) {
        return followRepository.countByFollowerId(userId);
    }

    /**
     * 获取粉丝数
     */
    @Transactional(readOnly = true)
    public int getFollowerCount(Long userId) {
        return followRepository.countByFollowingId(userId);
    }

    /**
     * 获取某人的关注列表
     */
    @Transactional(readOnly = true)
    public List<FollowResponse> getFollowingList(Long userId) {
        List<Follow> follows = followRepository.findByFollowerId(userId);
        return follows.stream()
                .map(f -> {
                    User follower = userRepository.findById(f.getFollowerId()).orElse(null);
                    User following = userRepository.findById(f.getFollowingId()).orElse(null);
                    return toFollowResponse(f, follower, following);
                })
                .toList();
    }

    /**
     * 获取某人的粉丝列表
     */
    @Transactional(readOnly = true)
    public List<FollowResponse> getFollowerList(Long userId) {
        List<Follow> follows = followRepository.findByFollowingId(userId);
        return follows.stream()
                .map(f -> {
                    User follower = userRepository.findById(f.getFollowerId()).orElse(null);
                    User following = userRepository.findById(f.getFollowingId()).orElse(null);
                    return toFollowResponse(f, follower, following);
                })
                .toList();
    }

    /**
     * 获取用户关注和粉丝统计
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getFollowStats(Long userId, Long currentUserId) {
        Map<String, Object> stats = new HashMap<>();
        stats.put("followingCount", getFollowingCount(userId));
        stats.put("followerCount", getFollowerCount(userId));
        if (currentUserId != null && !currentUserId.equals(userId)) {
            stats.put("isFollowing", isFollowing(currentUserId, userId));
        } else {
            stats.put("isFollowing", false);
        }
        return stats;
    }

    private FollowResponse toFollowResponse(Follow follow, User follower, User following) {
        return FollowResponse.builder()
                .id(follow.getId())
                .followerId(follow.getFollowerId())
                .followerUsername(follower != null ? follower.getUsername() : null)
                .followerAvatar(follower != null ? follower.getAvatarUrl() : null)
                .followingId(follow.getFollowingId())
                .followingUsername(following != null ? following.getUsername() : null)
                .followingAvatar(following != null ? following.getAvatarUrl() : null)
                .createdAt(follow.getCreatedAt())
                .build();
    }
}
