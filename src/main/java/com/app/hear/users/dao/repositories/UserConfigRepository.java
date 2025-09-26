package com.app.hear.users.dao.repositories;

import com.app.hear.users.dao.models.entities.UserConfigEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserConfigRepository extends JpaRepository<UserConfigEntity, Long> {}
