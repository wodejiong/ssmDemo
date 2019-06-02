package demo.wj.service;

import demo.wj.domain.Permission;

import java.util.List;

public interface PermissionService {
    List<Permission> findAll();

    void save(Permission permission);

    void deleteSelected(String[] ids);

    void deleteById(String id);

    Permission findById(String id);

    void updatePermission(Permission permission);
}
