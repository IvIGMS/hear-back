package com.app.hear.catalog.controllers;

import com.app.hear.api.ColorsApi;
import com.app.hear.catalog.services.CatalogColorService;
import com.app.hear.common.exceptions.utils.ControllerUtils;
import com.app.hear.model.ColorDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/v1")
@RequiredArgsConstructor
public class CatalogColorController extends ControllerUtils implements ColorsApi {
    private final CatalogColorService catalogColorService;

    @Override
    public ResponseEntity<List<ColorDTO>> getAllColors() {
        return ResponseEntity.ok(catalogColorService.getAllColors());
    }

    @Override
    public ResponseEntity<ColorDTO> getColorByCode(String code) {
        return ResponseEntity.ok(catalogColorService.getColorByCode(code));

    }
}
