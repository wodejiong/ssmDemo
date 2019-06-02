package demo.wj.service;

import demo.wj.domain.Permission;
import demo.wj.domain.Role;

import java.util.List;

public interface RoleService {
    List<Role> findAll();

    void save(Role role);

    Role findById(String id);

    List<Permission> findOtherPermissionByRoleId(String id);

    void addPermissionToRole(String roleId, String[] permissionIds);
}
