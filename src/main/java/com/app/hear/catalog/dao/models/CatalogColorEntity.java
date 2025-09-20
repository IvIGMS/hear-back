package com.app.hear.catalog.dao.models;

import com.app.hear.common.exceptions.utils.AuditableEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "catalog_colors")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CatalogColorEntity extends AuditableEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true, length = 50)
  private String name; // e.g., "Red"

  @Column(nullable = false, length = 7)
  private String hexCode; // e.g., "#FF0000"

  @Column(nullable = false, length = 2, unique = true)
  private String code; // e.g., "RO", "AZ"
}
