package com.nepbrain.technology.auth.repository;

import com.nepbrain.technology.auth.entity.RoleEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface RoleRepository extends JpaRepository<RoleEntity, UUID> {

    boolean existsByName(String name);

    /** Prefer this for token issuance — one SQL, no entity-graph cartesian product. */
    @Query(value = """
            SELECT DISTINCT p.code
            FROM permission p
            INNER JOIN role_permission rp ON rp.permission_id = p.id
            WHERE rp.role_id IN (:roleIds)
              AND p.code IS NOT NULL
              AND TRIM(p.code) <> ''
            """, nativeQuery = true)
    List<String> findPermissionCodesByRoleIds(@Param("roleIds") Collection<UUID> roleIds);

    @EntityGraph(attributePaths = "permissions")
    @Query("select distinct r from RoleEntity r where r.id in :ids")
    List<RoleEntity> findAllByIdWithPermissions(@Param("ids") Collection<UUID> ids);
}
