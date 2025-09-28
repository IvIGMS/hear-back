package com.app.hear.spaces.dao.repositories;

import com.app.hear.spaces.dao.models.entities.SpaceInvitationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpaceInvitationRepository extends JpaRepository<SpaceInvitationEntity, Long> {
  SpaceInvitationEntity findFirstBySpaceIdAndReceiverUserIdOrderByCreatedAtDesc(
      Long spaceId, Long receiverUserId);
}
