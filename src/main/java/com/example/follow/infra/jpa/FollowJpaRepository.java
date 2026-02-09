package com.example.follow.infra.jpa;

import com.example.user.infra.jpa.UserJpaEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FollowJpaRepository extends JpaRepository<FollowJpaEntity, FollowId> {
  boolean existsByIdFollowerIdAndIdFollowingId(long followerId, long followingId);

  long countByIdFollowingId(long userId);

  long countByIdFollowerId(long userId);

  void deleteByIdFollowerIdAndIdFollowingId(long followerId, long followingId);

  @Query(
      """
      select u
        from UserJpaEntity u
        join FollowJpaEntity f on f.follower.id = u.id
       where f.following.id = :userId
       order by f.createdAt desc
      """)
  List<UserJpaEntity> findFollowers(@Param("userId") long userId);

  @Query(
      """
      select u
        from UserJpaEntity u
        join FollowJpaEntity f on f.following.id = u.id
       where f.follower.id = :userId
       order by f.createdAt desc
      """)
  List<UserJpaEntity> findFollowing(@Param("userId") long userId);
}

