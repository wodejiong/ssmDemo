package demo.wj.controller;

import demo.wj.domain.Role;
import demo.wj.domain.UserInfo;
import demo.wj.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.security.RolesAllowed;
import java.util.List;

@Controller
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    @RequestMapping("/findAll.do")
//    @RolesAllowed("USER")
    public String findAll(Model model) {
        List<UserInfo> userList = userService.findAll();
        model.addAttribute("userList", userList);
        return "user-list";
    }

    @RequestMapping("/save.do")
    public String save(UserInfo userInfo) {
        userService.save(userInfo);
        return "redirect:/user/findAll.do";
    }

    @RequestMapping("/findById.do")
    public String findById(String id,Model model) {
        UserInfo userInfo=userService.findById(id);
        model.addAttribute("userInfo",userInfo);
        return "user-show1";
    }

    @RequestMapping("/findUserByIdAndAllRole.do")
    public String findUserByIdAndAllRole(String id,Model model) {
        UserInfo userInfo = userService.findById(id);
        List<Role> roles=userService.findOtherRolesByUserId(id);
        model.addAttribute("user", userInfo);
        model.addAttribute("roleList", roles);
        return "user-role-add";
    }

    @RequestMapping("/addRoleToUser.do")
    public String addRoleToUser(@RequestParam(name="userId") String userId, @RequestParam(name="ids") String[] roleIds) {
        userService.addRoleToUser(userId, roleIds);
        return "redirect:findAll.do";
    }
}
