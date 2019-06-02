package demo.wj.service.impl;

import demo.wj.dao.PermissionDao;
import demo.wj.domain.Permission;
import demo.wj.service.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PermissionServiceImpl implements PermissionService {
    @Autowired
    private PermissionDao permissionDao;

    @Override
    public List<Permission> findAll() {
        return permissionDao.findAll();
    }

    @Override
    public void save(Permission permission) {
        permissionDao.save(permission);
    }

    @Override
    public void deleteSelected(String[] ids) {
        for (String id : ids) {
            permissionDao.deleteById(id);
        }
    }

    @Override
    public void deleteById(String id) {
        permissionDao.deleteById(id);
    }

    @Override
    public Permission findById(String id) {
        return permissionDao.findById(id);
    }

    @Override
    public void updatePermission(Permission permission) {
        permissionDao.updatePermission(permission);

    }
}
