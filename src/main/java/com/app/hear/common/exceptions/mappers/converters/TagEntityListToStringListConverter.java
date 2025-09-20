package com.app.hear.common.exceptions.mappers.converters;

import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;
import com.app.hear.tags.dao.models.entities.TagEntity;
import java.util.List;
import java.util.stream.Collectors;

public class TagEntityListToStringListConverter implements Converter<List<TagEntity>, List<String>> {

    @Override
    public List<String> convert(MappingContext<List<TagEntity>, List<String>> context) {
        if (context.getSource() == null) {
            return null;
        }
        return context.getSource().stream()
                .map(TagEntity::getName)
                .collect(Collectors.toList());
    }
}

