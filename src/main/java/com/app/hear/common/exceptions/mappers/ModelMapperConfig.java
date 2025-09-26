package com.app.hear.common.exceptions.mappers;

import com.app.hear.common.exceptions.mappers.converters.TagEntityListToStringListConverter;
import com.app.hear.common.exceptions.mappers.converters.VoiceNoteEntityListToIntegerConverter;
import com.app.hear.model.SpaceDTO;
import com.app.hear.model.VoiceNoteDTO;
import com.app.hear.spaces.dao.models.entities.SpaceEntity;
import com.app.hear.voiceNotes.dao.models.entities.VoiceNoteEntity;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {

  // todo: sacar estos converters de aqui
  private final Converter<ZonedDateTime, OffsetDateTime> zonedToOffset =
      ctx -> {
        ZonedDateTime source = ctx.getSource();
        return (source != null) ? source.toOffsetDateTime() : null;
      };

  private final Converter<OffsetDateTime, ZonedDateTime> offsetToZoned =
      ctx -> {
        OffsetDateTime source = ctx.getSource();
        return (source != null) ? source.toZonedDateTime() : null;
      };

  @Bean
  public ModelMapper modelMapper() {
    ModelMapper mapper = new ModelMapper();
    configureUserMapping(mapper);
    return mapper;
  }

  private void configureUserMapping(ModelMapper mapper) {
    mapper
        .getConfiguration()
        .setFieldMatchingEnabled(true)
        .setFieldAccessLevel(org.modelmapper.config.Configuration.AccessLevel.PRIVATE)
        .setSkipNullEnabled(true);

    mapper.addConverter(zonedToOffset);
    mapper.addConverter(offsetToZoned);
    mapper.addConverter(new TagEntityListToStringListConverter());
    mapper.addConverter(new VoiceNoteEntityListToIntegerConverter());

    mapper
        .typeMap(VoiceNoteEntity.class, VoiceNoteDTO.class)
        .addMappings(
            m -> {
              m.map(src -> src.getSpace().getName(), VoiceNoteDTO::setSpaceName);
              m.map(src -> src.getSpace().getColor().getCode(), VoiceNoteDTO::setCodeSpaceColor);
              m.using(new TagEntityListToStringListConverter())
                  .map(src -> src.getSpace().getTags(), VoiceNoteDTO::setTags);
            });
    mapper
        .typeMap(SpaceEntity.class, SpaceDTO.class)
        .addMappings(
            m -> {
              m.map(src -> src.getColor().getCode(), SpaceDTO::setColorCode);
              m.using(new VoiceNoteEntityListToIntegerConverter())
                  .map(SpaceEntity::getVoiceNotes, SpaceDTO::setTotalVoiceNotes);
            });
  }
}
