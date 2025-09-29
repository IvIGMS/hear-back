package com.app.hear.spaces.dao.repositories;

import com.app.hear.spaces.dao.models.entities.SpaceInvitationEntity;
import com.app.hear.spaces.dao.models.enums.InvitationStatus;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SpaceInvitationRepository extends JpaRepository<SpaceInvitationEntity, Long> {
  SpaceInvitationEntity findFirstBySpaceIdAndReceiverUserIdOrderByCreatedAtDesc(
      Long spaceId, Long receiverUserId);

  SpaceInvitationEntity findBySpace_IdAndSenderUser_IdAndInvitationStatus(
      Long spaceId, Long senderUserId, InvitationStatus invitationStatus);

  @Query(
      """
          SELECT si
          FROM SpaceInvitationEntity si
          WHERE si.receiverUser.id = :receiverUserId
          and si.invitationStatus = 'PENDING'

          """)
  public List<SpaceInvitationEntity> getPendingInvitations(
      @Param("receiverUserId") Long receiverUserId);
}
