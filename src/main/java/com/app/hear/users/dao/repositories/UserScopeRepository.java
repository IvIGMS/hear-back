package com.app.hear.users.dao.repositories;

import com.app.hear.users.dao.models.entities.UserScopeEntity;
import com.app.hear.users.dao.models.enums.Tier;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserScopeRepository extends JpaRepository<UserScopeEntity, Long> {
  Optional<UserScopeEntity> findByTier(Tier tier);
}
