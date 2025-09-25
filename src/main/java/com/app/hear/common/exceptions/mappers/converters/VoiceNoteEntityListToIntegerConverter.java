package com.app.hear.common.exceptions.mappers.converters;

import com.app.hear.voiceNotes.dao.models.entities.VoiceNoteEntity;
import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;

import java.util.List;

public class VoiceNoteEntityListToIntegerConverter
        implements Converter<List<VoiceNoteEntity>, Integer> {

  @Override
  public Integer convert(MappingContext<List<VoiceNoteEntity>, Integer> context) {
    List<VoiceNoteEntity> source = context.getSource();
    return (source != null) ? source.size() : 0;
  }
}
