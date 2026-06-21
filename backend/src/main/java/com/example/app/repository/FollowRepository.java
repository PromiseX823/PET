package com.example.app.repository;

import com.example.app.entity.Follow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FollowRepository extends JpaRepository<Follow, Long> {

    // 查询某人关注了哪些人
    List<Follow> findByFollowerId(Long followerId);

    // 查询某人的粉丝
    List<Follow> findByFollowingId(Long followingId);

    // 统计某人的关注数
    int countByFollowerId(Long followerId);

    // 统计某人的粉丝数
    int countByFollowingId(Long followingId);

    // 查询特定关注关系
    Optional<Follow> findByFollowerIdAndFollowingId(Long followerId, Long followingId);

    // 删除关注关系
    void deleteByFollowerIdAndFollowingId(Long followerId, Long followingId);

    // 检查是否已关注
    boolean existsByFollowerIdAndFollowingId(Long followerId, Long followingId);
}
