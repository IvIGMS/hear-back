package com.app.hear.voiceNotes.services;

import com.app.hear.common.exceptions.ConflictException;
import com.app.hear.common.exceptions.NotFoundException;
import com.app.hear.model.VoiceNoteDTO;
import com.app.hear.security.dao.models.entities.UserEntity;
import com.app.hear.spaces.dao.models.entities.SpaceEntity;
import com.app.hear.spaces.dao.models.entities.UserSpaceRole;
import com.app.hear.spaces.dao.models.enums.RoleUserSpace;
import com.app.hear.spaces.services.SpaceService;
import com.app.hear.spaces.services.UserSpaceRoleService;
import com.app.hear.users.services.UserService;
import com.app.hear.voiceNotes.dao.models.entities.VoiceNoteEntity;
import com.app.hear.voiceNotes.dao.repositories.VoiceNoteRepository;
import com.app.hear.voiceNotes.dtos.StreamingResponse;
import com.mpatric.mp3agic.Mp3File;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j
public class VoiceNoteService {
  private final VoiceNoteRepository voiceNoteRepository;
  private final SpaceService spaceService;
  private final UserService userService;
  private final UserSpaceRoleService userSpaceRoleService;
  private final ModelMapper modelMapper;

  @Transactional
  public void uploadNoteVoice(
      MultipartFile file, Long spaceId, String nombreAudio, String description, Long ownerId) {
    try {
      SpaceEntity space = spaceService.getSpaceEntityById(spaceId);

      testMaximunVoiceNotesUser(ownerId, spaceId);
      // Guardar con el nombre original + hash + extensión original
      String originalFilename = file.getOriginalFilename();
      String extension =
          originalFilename != null && originalFilename.contains(".")
              ? originalFilename.substring(originalFilename.lastIndexOf("."))
              : ".opus"; // por defecto .opus si no viene extensión

      String nombreAudioTransformed = nombreAudio + createHash() + extension;
      String path = saveFile(file, space.getName(), nombreAudioTransformed);
      log.info("El archivo se ha guardado correctamente en el Gestor Documental");

      Path audioPath = Paths.get(path);

      // Si es .opus -> convertir a .mp3
      if (extension.equalsIgnoreCase(".opus")) {
        audioPath = convertOpusToMp3(audioPath);
        // actualizar nombre y path porque ahora es mp3
        nombreAudioTransformed = nombreAudioTransformed.replace(".opus", ".mp3");
        path = audioPath.toString();
      }

      long duration = checkDuration(audioPath);

      userSpaceRoleService.getUserSpaceRoleByUserIdAndSpaceId(ownerId, spaceId);

      VoiceNoteEntity voiceNoteEntityToSave =
          VoiceNoteEntity.builder()
              .nombre(nombreAudioTransformed)
              .description(description)
              .duration((int) duration)
              .storagePath(path)
              .space(space)
              .uploadedBy(userService.getUserEntityById(ownerId))
              .build();
      voiceNoteRepository.saveAndFlush(voiceNoteEntityToSave);
      log.info("El archivo se ha guardado correctamente en la base de datos");
    } catch (ConflictException e) {
      throw new ConflictException(
          "No puedes crear mas notas de voz para este scope. Has llegado al límite para un usuario gratuito. Actualiza a un plan premium.");
    } catch (Exception e) {
      throw new RuntimeException("Error desconocido", e);
    }
  }

  private void testMaximunVoiceNotesUser(Long ownerId, Long spaceId) {
    UserEntity user = userService.getUserEntityById(ownerId);
    int maxVoiceNotesSpace = user.getUserConfig().getScope().getNumMaxVoiceNotesByScope();
    int counterSpacesAdmin = spaceService.getSpaceById(ownerId, spaceId).getTotalVoiceNotes();

    if (maxVoiceNotesSpace == counterSpacesAdmin || maxVoiceNotesSpace < counterSpacesAdmin) {
      throw new ConflictException(
          "No puedes crear mas notas de voz para este scope. Has llegado al límite para un usuario gratuito. Actualiza a un plan premium.");
    }
  }

  private Path convertOpusToMp3(Path opusPath) throws Exception {
    String mp3Path = opusPath.toString().replace(".opus", ".mp3");

    ProcessBuilder pb =
        new ProcessBuilder(
            "ffmpeg",
            "-y",
            "-i",
            opusPath.toString(),
            "-acodec",
            "libmp3lame",
            "-q:a",
            "2",
            mp3Path);
    pb.redirectErrorStream(true);
    Process process = pb.start();
    int exitCode = process.waitFor();

    if (exitCode != 0) {
      throw new RuntimeException("Error al convertir el archivo opus a mp3");
    }
    log.info("Conversión de opus a mp3 realizada correctamente: {}", mp3Path);
    return Paths.get(mp3Path);
  }

  private long checkDuration(Path filePath) throws Exception {
    Mp3File mp3file = new Mp3File(filePath.toFile());
    long duration = mp3file.getLengthInSeconds();
    if (duration > 180L) {
      throw new ConflictException("La duración del audio no puede ser de mas de 90 segundos");
    }
    return duration;
  }

  private void deleteFile(String spaceName, String nombreAudio) throws IOException {
    String userHome = System.getProperty("user.home");
    Path folderPath = Paths.get(userHome, "audio_data", spaceName);
    Path filePath = folderPath.resolve(nombreAudio);

    // Borrar archivo
    Files.deleteIfExists(filePath);

    // Vemos si hay archivos ocultos
    boolean hasVisibleFiles = false;
    try (DirectoryStream<Path> entries = Files.newDirectoryStream(folderPath)) {
      for (Path entry : entries) {
        String name = entry.getFileName().toString();
        if (!name.startsWith(".")) { // si no es oculto
          hasVisibleFiles = true;
          break;
        }
      }
    }

    // Si hay los borramos
    if (!hasVisibleFiles) {
      try (DirectoryStream<Path> entries = Files.newDirectoryStream(folderPath)) {
        for (Path entry : entries) {
          Files.deleteIfExists(entry);
        }
      }
      Files.delete(folderPath);
    }
  }

  private String saveFile(MultipartFile file, String spaceName, String nombreAudio)
      throws IOException {
    // Carpeta base en el home del usuario
    String userHome = System.getProperty("user.home");
    Path uploadPath = Paths.get(userHome, "audio_data", spaceName);

    // Crear carpeta si no existe
    if (!Files.exists(uploadPath)) {
      Files.createDirectories(uploadPath);
    }

    // Ruta completa del archivo
    Path filePath = uploadPath.resolve(nombreAudio);

    // Copiar contenido (más robusto que transferTo)
    Files.copy(file.getInputStream(), filePath, java.nio.file.StandardCopyOption.REPLACE_EXISTING);

    return filePath.toString();
  }

  private String createHash() {
    try {
      String input = UUID.randomUUID().toString();

      MessageDigest digest = MessageDigest.getInstance("SHA-256");
      byte[] hashBytes = digest.digest(input.getBytes());

      StringBuilder hex = new StringBuilder();
      for (byte b : hashBytes) {
        hex.append(String.format("%02x", b));
      }

      return "_" + hex.substring(0, 8); // 8 dígitos
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException("Error creando hash", e);
    }
  }

  public Page<VoiceNoteDTO> getVoiceNotes(
      Integer pageNumberQueryParam,
      Integer pageSizeQueryParam,
      String sortByQueryParam,
      String spaceName,
      String voiceNoteName) {
    Pageable pageable =
        PageRequest.of(pageNumberQueryParam - 1, pageSizeQueryParam, Sort.by(sortByQueryParam));

    Page<VoiceNoteEntity> voiceNoteEntities =
        voiceNoteRepository.getVoiceNotes(spaceName, voiceNoteName, pageable);

    return voiceNoteEntities.map(vn -> modelMapper.map(vn, VoiceNoteDTO.class));
  }

  public StreamingResponse getVoiceNoteStream(Long voiceNoteId, String rangeHeader)
      throws IOException {
    VoiceNoteEntity voiceNoteEntity =
        voiceNoteRepository
            .findById(voiceNoteId)
            .orElseThrow(() -> new NotFoundException("Nota de voz no encontrada"));

    Path filePath = Paths.get(voiceNoteEntity.getStoragePath());
    if (!Files.exists(filePath)) {
      throw new NotFoundException("Archivo no encontrado en el servidor");
    }

    File file = filePath.toFile();
    long fileLength = file.length();

    // Caso sin Range: devolvemos el archivo completo
    if (rangeHeader == null) {
      return StreamingResponse.builder()
          .resource(new FileSystemResource(file))
          .contentLength(fileLength)
          .partial(false)
          .build();
    }

    // Caso con Range
    String[] ranges = rangeHeader.replace("bytes=", "").split("-");
    long start = Long.parseLong(ranges[0]);
    long end =
        (ranges.length > 1 && !ranges[1].isEmpty()) ? Long.parseLong(ranges[1]) : fileLength - 1;

    if (end >= fileLength) {
      end = fileLength - 1;
    }

    long contentLength = end - start + 1;

    InputStream inputStream = Files.newInputStream(file.toPath());
    inputStream.skip(start);

    return StreamingResponse.builder()
        .resource(new InputStreamResource(inputStream))
        .contentLength(contentLength)
        .contentRange("bytes " + start + "-" + end + "/" + fileLength)
        .partial(true)
        .build();
  }

  public List<VoiceNoteDTO> getVoiceNotesBySpaceId(Long spaceId, String voiceNoteNameQueryParam) {
    List<VoiceNoteEntity> voiceNoteEntities =
        voiceNoteRepository.getVoiceNotesBySpaceId(spaceId, voiceNoteNameQueryParam);
    return voiceNoteEntities.stream().map(vn -> modelMapper.map(vn, VoiceNoteDTO.class)).toList();
  }

  @Transactional
  public void deleteVoiceNoteById(Long voiceNoteId, Long ownerId) {
    VoiceNoteEntity voiceNoteEntity =
        voiceNoteRepository
            .findById(voiceNoteId)
            .orElseThrow(
                () -> new NotFoundException("El voice note con id " + voiceNoteId + " no existe."));
    SpaceEntity space = spaceService.getSpaceEntityByVoiceNoteId(voiceNoteId);
    UserSpaceRole userSpaceRole =
        userSpaceRoleService.getUserSpaceRoleByUserIdAndSpaceId(ownerId, space.getId());
    if (userSpaceRole.getRole().name().equals(RoleUserSpace.ADMIN.name())) {
      voiceNoteRepository.deleteById(voiceNoteId);
      try {
        deleteFile(space.getName(), voiceNoteEntity.getNombre());
      } catch (NoSuchFileException e) {
        throw new ConflictException(
            "No se ha podido borrar el archivo porque no existe en el gestor documental");
      } catch (Exception e) {
        throw new ConflictException(
            "No se ha podido borrar el archivo del gestor documental. Error desconocido");
      }
    } else {
      throw new ConflictException(
          "No tienes perimoss para borrar este audio, eres member, necesitas ser admin");
    }
  }
}
