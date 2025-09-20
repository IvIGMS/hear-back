package com.app.hear.catalog.dao.repositories;

import com.app.hear.catalog.dao.models.CatalogColorEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CatalogColorRepository extends JpaRepository<CatalogColorEntity, Long> {
  Optional<CatalogColorEntity> findByCode(String code);
}
