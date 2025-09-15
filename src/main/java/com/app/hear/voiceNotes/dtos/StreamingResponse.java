package com.app.hear.voiceNotes.dtos;

import lombok.Builder;
import lombok.Data;
import org.springframework.core.io.Resource;

@Data
@Builder
public class StreamingResponse {
  private Resource resource;
  private long contentLength;
  private String contentRange;
  private boolean partial;
}
