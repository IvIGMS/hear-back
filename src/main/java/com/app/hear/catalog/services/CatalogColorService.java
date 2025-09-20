package com.app.hear.catalog.services;

import com.app.hear.catalog.dao.models.CatalogColorEntity;
import com.app.hear.catalog.dao.repositories.CatalogColorRepository;
import com.app.hear.common.exceptions.NotFoundException;
import com.app.hear.model.ColorDTO;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CatalogColorService {
  private final CatalogColorRepository catalogColorRepository;
  private final ModelMapper modelMapper;

  public List<ColorDTO> getAllColors() {
    List<CatalogColorEntity> colors = catalogColorRepository.findAll();
    return colors.stream().map(color -> modelMapper.map(color, ColorDTO.class)).toList();
  }

  public ColorDTO getColorByCode(String code) {
    CatalogColorEntity catalogColorEntity =
        catalogColorRepository
            .findByCode(code)
            .orElseThrow(
                () -> new NotFoundException("No se ha encontrado el color con codigo: " + code));
    return modelMapper.map(catalogColorEntity, ColorDTO.class);
  }
}
