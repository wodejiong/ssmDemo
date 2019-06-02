package demo.wj.service;

import demo.wj.domain.Role;
import demo.wj.domain.UserInfo;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UserService extends UserDetailsService{
    List<UserInfo> findAll();

    void save(UserInfo userInfo);

    UserInfo findById(String id);

    List<Role> findOtherRolesByUserId(String id);

    void addRoleToUser(String userId, String[] roleIds);
}
