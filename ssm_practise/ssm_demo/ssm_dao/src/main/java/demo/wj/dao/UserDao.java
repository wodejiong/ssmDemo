package demo.wj.dao;

import demo.wj.domain.Role;
import demo.wj.domain.UserInfo;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface UserDao {

    @Select("select * from users where username=#{username}")
    @Results({
            @Result(id = true, property = "id", column = "id"),
            @Result(property = "username", column = "username"),
            @Result(property = "email", column = "email"),
            @Result(property = "password", column = "password"),
            @Result(property = "phoneNum", column = "phoneNum"),
            @Result(property = "status", column = "status"),
            @Result(property="roles",javaType=java.util.List.class,column = "id",
                    many=@Many(select="demo.wj.dao.RoleDao.findByUserId"))
    })
    UserInfo findByUsername(String username);

    @Select("select * from users")
    List<UserInfo> findAll();
    @Insert("insert into users(EMAIL,USERNAME,PASSWORD,PHONENUM,STATUS) values(#{email},#{username},#{password},#{phoneNum},#{status})")
    void save(UserInfo userInfo);


    @Select("select * from users where id=#{id}")
    @Results({
            @Result(id = true, property = "id", column = "id"),
            @Result(property = "username", column = "username"),
            @Result(property = "email", column = "email"),
            @Result(property = "password", column = "password"),
            @Result(property = "phoneNum", column = "phoneNum"),
            @Result(property = "status", column = "status"),
            @Result(property="roles",javaType=java.util.List.class,column = "id",
                    many=@Many(select="demo.wj.dao.RoleDao.findByUserId"))
    })
    UserInfo findById(String id);

    @Select("select * from role where id not in (select RoleId from users_role where userId = #{id})")
    List<Role> findOtherRolesByUserId(String id);


    @Insert("insert into users_role  values(#{userId},#{roleId})")
    void addRoleToUser(@Param("userId") String userId, @Param("roleId") String roleId);
}
