package demo.wj.controller;

import demo.wj.domain.Permission;
import demo.wj.domain.Role;
import demo.wj.service.RoleService;
import demo.wj.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
@RequestMapping("/role")
public class RoleController {
    @Autowired
    private RoleService roleService;

    @RequestMapping("/findAll.do")
    public ModelAndView findAll() {
        ModelAndView mv=new ModelAndView();
        List<Role> roles= roleService.findAll();
        mv.addObject("roleList", roles);
        mv.setViewName("role-list");
        return mv;

    }

    @RequestMapping("/save.do")
    public String save(Role role) {
        roleService.save(role);
        return "redirect:/role/findAll.do";
    }
    @RequestMapping("/findRoleAndOtherPermissionsById.do")
    public String findRoleAndOtherPermissionsById(String id,Model model) {
       Role role= roleService.findById(id);
       List<Permission> permissions= roleService.findOtherPermissionByRoleId(id);
        model.addAttribute("role", role);
        model.addAttribute("permissions", permissions);
        return "role-permission-add";
    }

    @RequestMapping("/addPermissionToRole.do")
    public String addPermissionToRole(@RequestParam(name="roleId") String roleId, @RequestParam(name="ids") String[] permissionIds) {
        roleService.addPermissionToRole(roleId, permissionIds);
        return "redirect:findAll.do";
    }
}
