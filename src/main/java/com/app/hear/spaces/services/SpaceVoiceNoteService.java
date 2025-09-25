package com.app.hear.spaces.services;

import com.app.hear.model.SpaceDTO;
import com.app.hear.voiceNotes.services.VoiceNoteService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SpaceVoiceNoteService {
  private final SpaceService spaceService;
  private final VoiceNoteService voiceNoteService;

  public com.app.hear.model.SpaceListVoiceNotesDTO getSpaceVoiceNotesById(
      Long ownerId, Long spaceId, String voiceNoteNameQueryParam) {
    SpaceDTO spaceDTO = spaceService.getSpaceById(ownerId, spaceId);
    List<com.app.hear.model.VoiceNoteDTO> voiceNoteDTOList =
        voiceNoteService.getVoiceNotesBySpaceId(spaceId, voiceNoteNameQueryParam);

    return com.app.hear.model.SpaceListVoiceNotesDTO.builder()
        .space(spaceDTO)
        .voiceNotes(voiceNoteDTOList)
        .build();
  }
}
