package com.app.hear.users.dao.repositories;

import com.app.hear.security.dao.models.entities.UserEntity;
import com.app.hear.security.dao.models.enums.RoleUserEnum;
import com.app.hear.users.dao.dto.UserDownDto;
import com.app.hear.users.dto.projections.UserProjectionDTO;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
  Optional<UserEntity> findByEmail(String email);

  @Query(
      value =
          " SELECT u.* from users u "
              + " INNER JOIN companies c ON c.id = u.company_id "
              + " WHERE c.owner_id = :ownerId",
      nativeQuery = true)
  List<UserEntity> getUsersByOwnerId(@Param("ownerId") Long ownerId);

  List<UserEntity> findByRole(RoleUserEnum role);

  @Query(
      value =
          """
            SELECT
            u.email as email,
            u.firstname as name,
            u.lastname as lastName,
            s.company_id as companyId,
            s.user_id as userId
            FROM users u
            INNER JOIN subscriptions s ON s.user_id = u.id
            WHERE u.role = 'USER'
              AND s.end_date = (
                  SELECT MAX(s2.end_date)
                  FROM subscriptions s2
                  WHERE s2.user_id = u.id
              )
            AND s.end_date <= :now
            AND s.is_active = true
            """,
      nativeQuery = true)
  List<UserDownDto> usersDownToday(@Param("now") LocalDate now);

  @Query(
      value =
          """
          SELECT
            u.id as id,
            u.email as email,
            u.firstname as firstname,
            u.lastname as lastname,
            u.is_active as isEmailActive,
            u.role as role
          FROM
            users u
          WHERE
            u.id NOT IN
          (SELECT
          	user_id
          	FROM user_space_roles usr
          	WHERE usr.space_id = :spaceId
          )

          """,
      nativeQuery = true)
  List<UserProjectionDTO> getUserCanBeInvitedToThisSpace(@Param("spaceId") Long spaceId);
}
