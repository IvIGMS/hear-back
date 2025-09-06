package com.app.hear.spaces.dao.repositories;

import com.app.hear.spaces.dao.models.entities.SpaceEntity;
import com.app.hear.spaces.dao.models.entities.UserSpaceRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserSpaceRoleRepository extends JpaRepository<UserSpaceRole, Long> {
}
