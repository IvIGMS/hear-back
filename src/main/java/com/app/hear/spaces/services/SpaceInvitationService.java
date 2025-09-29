package com.app.hear.spaces.services;

import com.app.hear.common.exceptions.ConflictException;
import com.app.hear.model.*;
import com.app.hear.notifications.services.NotificationService;
import com.app.hear.security.dao.models.entities.UserEntity;
import com.app.hear.spaces.dao.models.entities.SpaceEntity;
import com.app.hear.spaces.dao.models.entities.SpaceInvitationEntity;
import com.app.hear.spaces.dao.models.enums.InvitationStatus;
import com.app.hear.spaces.dao.repositories.SpaceInvitationRepository;
import com.app.hear.users.services.UserService;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class SpaceInvitationService {
  private final SpaceInvitationRepository spaceInvitationRepository;
  private final SpaceService spaceService;
  private final UserService userService;
  private final ModelMapper modelMapper;
  private final UserSpaceRoleService userSpaceRoleService;
  private final NotificationService notificationService;

  public SpaceInvitationDTO sendInvitationToMySpace(
      UserSpaceRoleRequestDTO userSpaceRoleRequestDTO, Long senderId) {
    UserEntity senderUser = userService.getUserEntityById(senderId);
    UserEntity receiverUser = userService.getUserEntityById(userSpaceRoleRequestDTO.getUserId());
    SpaceEntity spaceEntity = spaceService.getSpaceEntityById(userSpaceRoleRequestDTO.getSpaceId());

    if (senderUser.getId().equals(receiverUser.getId())) {
      throw new ConflictException("El usuario sender y receiver no puede ser el mismo");
    }

    if (!spaceService.areYouAdminOfTheSpace(spaceEntity.getId(), senderId)) {
      throw new ConflictException("El usuario no es admin en este space");
    }

    SpaceInvitationEntity lastInvitation =
        spaceInvitationRepository.findFirstBySpaceIdAndReceiverUserIdOrderByCreatedAtDesc(
            userSpaceRoleRequestDTO.getSpaceId(), userSpaceRoleRequestDTO.getUserId());

    if (Objects.nonNull(lastInvitation)) {
      if (lastInvitation.getInvitationStatus().equals(InvitationStatus.PENDING)) {
        throw new ConflictException(
            "El usuario ya tiene una peticion pendiente para esta space. Si quieres enviar otra debe rechazarla");
      } else if (lastInvitation.getInvitationStatus().equals(InvitationStatus.ACCEPTED)) {
        // Si ya existe
        if (userSpaceRoleService.existRoleByUserIdAndSpaceId(
            userSpaceRoleRequestDTO.getUserId(), userSpaceRoleRequestDTO.getSpaceId())) {
          throw new ConflictException(
              "El usuario ya ha aceptado una invitacion para este space y sigue vigente.");
        }
      }
    }

    SpaceInvitationEntity invitationToSave =
        SpaceInvitationEntity.builder()
            .space(spaceEntity)
            .senderUser(senderUser)
            .receiverUser(receiverUser)
            .roleUserSpace(userSpaceRoleRequestDTO.getRole())
            .invitationStatus(InvitationStatus.PENDING)
            .build();
    // Guardar invitación
    SpaceInvitationEntity savedInvitation = spaceInvitationRepository.save(invitationToSave);
    // Guardar notificación. De momento solo guardamos, no enviamos por correo.
    notificationService.createNotification(
        NotificationDTO.builder()
            .isSended(false)
            .receiverUserId(userSpaceRoleRequestDTO.getUserId())
            .notificationTypeCode("INVITATION_TYPE")
            .message("Dummy message")
            .build());
    return modelMapper.map(savedInvitation, SpaceInvitationDTO.class);
  }

  public List<SpaceInvitationDTO> getPendingInvitations(Long guestId) {
    List<SpaceInvitationEntity> spaceInvitationEntities =
        spaceInvitationRepository.getPendingInvitations(guestId);
    return spaceInvitationEntities.stream()
        .map(inv -> modelMapper.map(inv, SpaceInvitationDTO.class))
        .toList();
  }

  @Transactional
  public void answerPendingInvitation(
      Long guestId, SpaceInvitationAnswerRequestDTO spaceInvitationAnswerRequestDTO) {
    SpaceInvitationEntity spaceInvitationEntity =
        spaceInvitationRepository.findBySpace_IdAndSenderUser_IdAndInvitationStatus(
            spaceInvitationAnswerRequestDTO.getSpaceId(),
            spaceInvitationAnswerRequestDTO.getUserId(),
            InvitationStatus.PENDING);
    if (!guestId.equals(spaceInvitationEntity.getReceiverUser().getId())) {
      throw new ConflictException(
          "El usuario que realiza la acción no tiene permisos sobre esta invitación");
    }

    if (spaceInvitationAnswerRequestDTO.getAnswer().equals(InvitationStatusResponse.REJECTED)) {
      spaceInvitationEntity.setInvitationStatus(InvitationStatus.REJECTED);
      log.info("El usuario ha decidido rechazar la invitación");
    } else if (spaceInvitationAnswerRequestDTO
        .getAnswer()
        .equals(InvitationStatusResponse.ACCEPTED)) {
      spaceInvitationEntity.setInvitationStatus(InvitationStatus.ACCEPTED);
      // Creamos el DTO de Role
      UserSpaceRoleRequestDTO role =
          UserSpaceRoleRequestDTO.builder()
              .userId(spaceInvitationEntity.getReceiverUser().getId())
              .spaceId(spaceInvitationEntity.getSpace().getId())
              .role(spaceInvitationEntity.getRoleUserSpace())
              .build();

      userSpaceRoleService.createUserSpaceRole(role, spaceInvitationEntity.getSenderUser().getId());
      log.info("El usuario ha decidido aceptar la invitación");
    }
  }
}
