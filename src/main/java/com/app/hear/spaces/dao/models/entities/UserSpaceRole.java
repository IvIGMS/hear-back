package com.app.hear.spaces.dao.models.entities;

import com.app.hear.common.exceptions.utils.AuditableEntity;
import com.app.hear.security.dao.models.entities.UserEntity;
import com.app.hear.spaces.dao.models.enums.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_space_roles")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSpaceRole extends AuditableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private Role role; // ADMIN, MEMBER

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @ManyToOne
    @JoinColumn(name = "space_id")
    private SpaceEntity space;
}
