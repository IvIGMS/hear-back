package com.app.hear.voiceNotes.controllers;

import static com.app.hear.common.exceptions.utils.ControllerUtilsConstants.STRING_NO_PREMISSIONS;

import com.app.hear.api.VoiceNotesApi;
import com.app.hear.common.exceptions.utils.ControllerUtils;
import com.app.hear.common.exceptions.utils.UnauthorizedException;
import com.app.hear.model.VoiceNoteDTO;
import com.app.hear.voiceNotes.dtos.StreamingResponse;
import com.app.hear.voiceNotes.services.VoiceNoteService;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("api/v1")
@RequiredArgsConstructor
public class VoiceNoteController extends ControllerUtils implements VoiceNotesApi {
  private final VoiceNoteService service;

  @Override
  public ResponseEntity<Void> uploadNoteVoice(
      MultipartFile file, Long spaceId, String nombre, String description) {
    if (!checkIsUser()) {
      throw new UnauthorizedException(STRING_NO_PREMISSIONS);
    }
    Long ownerId = getAllClaims().get("user_id", Long.class);
    service.uploadNoteVoice(file, spaceId, nombre, description, ownerId);

    return ResponseEntity.noContent().build();
  }

  @Override
  public ResponseEntity<List<VoiceNoteDTO>> getVoiceNotes(
      Integer pageNumberQueryParam,
      Integer pageSizeQueryParam,
      String sortByQueryParam,
      String spaceNameQueryParam) {
    Page<VoiceNoteDTO> results =
        service.getVoiceNotes(
            pageNumberQueryParam, pageSizeQueryParam, sortByQueryParam, spaceNameQueryParam);

    return responseOkPagination(ResponseEntity.ok(), results).body(results.getContent());
  }

  @Override
  public ResponseEntity<Resource> streamVoiceNote(Long id, String range) {
    try {
      StreamingResponse stream = service.getVoiceNoteStream(id, range);
      ResponseEntity.BodyBuilder responseBuilder =
          stream.isPartial()
              ? ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
              : ResponseEntity.ok();

      if (stream.isPartial()) {
        responseBuilder.header("Content-Range", stream.getContentRange());
        responseBuilder.header("Accept-Ranges", "bytes");
      }
      return responseBuilder
          .contentLength(stream.getContentLength())
          .contentType(MediaType.valueOf("audio/mpeg"))
          .body(stream.getResource());

    } catch (IOException e) {
      System.out.println("QUe no esta cojones");
      return null;
    }
  }
}
