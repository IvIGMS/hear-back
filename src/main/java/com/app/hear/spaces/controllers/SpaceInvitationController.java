package com.app.hear.spaces.controllers;

import static com.app.hear.common.exceptions.utils.ControllerUtilsConstants.STRING_NO_PREMISSIONS;

import com.app.hear.api.SpaceInvitationsApi;
import com.app.hear.common.exceptions.utils.ControllerUtils;
import com.app.hear.common.exceptions.utils.UnauthorizedException;
import com.app.hear.model.SpaceInvitationAnswerRequestDTO;
import com.app.hear.model.SpaceInvitationDTO;
import com.app.hear.spaces.services.SpaceInvitationService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1")
@RequiredArgsConstructor
public class SpaceInvitationController extends ControllerUtils implements SpaceInvitationsApi {
  private final SpaceInvitationService spaceInvitationService;

  @Override
  public ResponseEntity<List<SpaceInvitationDTO>> getPendingInvitations() {
    if (!checkIsUser()) {
      throw new UnauthorizedException(STRING_NO_PREMISSIONS);
    }
    Long guestId = getAllClaims().get("user_id", Long.class);
    return ResponseEntity.ok(spaceInvitationService.getPendingInvitations(guestId));
  }

  @Override
  public ResponseEntity<Void> answerPendingInvitation(
      SpaceInvitationAnswerRequestDTO spaceInvitationAnswerRequestDTO) {
    if (!checkIsUser()) {
      throw new UnauthorizedException(STRING_NO_PREMISSIONS);
    }
    Long guestId = getAllClaims().get("user_id", Long.class);
    spaceInvitationService.answerPendingInvitation(guestId, spaceInvitationAnswerRequestDTO);
    return ResponseEntity.noContent().build();
  }
}
