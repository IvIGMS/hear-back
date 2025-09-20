package com.app.hear.catalog.dao.repositories;

import com.app.hear.catalog.dao.models.CatalogColorEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CatalogColorRepository extends JpaRepository<CatalogColorEntity, Long> {
  Optional<CatalogColorEntity> findByCode(String code);
}
