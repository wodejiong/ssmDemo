package demo.wj.service.impl;

import demo.wj.dao.RoleDao;
import demo.wj.domain.Permission;
import demo.wj.domain.Role;
import demo.wj.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleServiceImpl implements RoleService{
    @Autowired
    private RoleDao roleDao;

    @Override
    public List<Role> findAll() {
        return roleDao.findAll();
    }

    @Override
    public void save(Role role) {
        roleDao.save(role);
    }

    @Override
    public Role findById(String id) {
        return roleDao.findById(id);
    }

    @Override
    public List<Permission> findOtherPermissionByRoleId(String id) {
        return roleDao.findOtherPermissionByRoleId(id);
    }

    @Override
    public void addPermissionToRole(String roleId, String[] permissionIds) {
        for (String permissionId : permissionIds) {

            roleDao.addPermissionToRole(roleId,permissionId);
        }
    }
}
