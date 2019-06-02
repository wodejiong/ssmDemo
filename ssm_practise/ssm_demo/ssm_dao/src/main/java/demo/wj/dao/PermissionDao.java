package demo.wj.dao;

import demo.wj.domain.Permission;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

public interface PermissionDao {

    @Select("select * from permission where id in (select permissionId from ROLE_PERMISSION where roleId =#{RoleId})")
    List<Permission> findByRoleId(String RoleId);

    @Select("select * from permission")
    List<Permission> findAll();

    @Insert("insert into permission(permissionName,url) values(#{permissionName},#{url})")
    void save(Permission permission);

    @Delete("delete from permission where id = #{id}")
    void deleteById(String id);

    @Select("select * from permission where id = #{id}")
    Permission findById(String id);

    @Update("update permission set permissionName=#{permissionName},url=#{url} where id = #{id}")
    void updatePermission(Permission permission);
}
