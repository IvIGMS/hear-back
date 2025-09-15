package com.app.hear.voiceNotes.controllers;

import static com.app.hear.common.exceptions.utils.ControllerUtilsConstants.STRING_NO_PREMISSIONS;

import com.app.hear.api.VoiceNotesApi;
import com.app.hear.common.exceptions.utils.ControllerUtils;
import com.app.hear.common.exceptions.utils.UnauthorizedException;
import com.app.hear.voiceNotes.services.VoiceNoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("api/v1")
@RequiredArgsConstructor
public class VoiceNoteController extends ControllerUtils implements VoiceNotesApi {
  private final VoiceNoteService service;

  @Override
  public ResponseEntity<Void> uploadNoteVoice(MultipartFile file, Long spaceId, String nombre, String description) {
    if (!checkIsUser()) {
      throw new UnauthorizedException(STRING_NO_PREMISSIONS);
    }
    Long ownerId = getAllClaims().get("user_id", Long.class);
    service.uploadNoteVoice(file, spaceId, nombre, description, ownerId);

    return ResponseEntity.noContent().build();
  }
}
