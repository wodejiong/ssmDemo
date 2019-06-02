package demo.wj.controller;

import com.sun.xml.internal.bind.v2.model.core.ID;
import demo.wj.domain.Permission;
import demo.wj.service.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
@RequestMapping("/permission")
public class PermissionController {
    @Autowired
    private PermissionService permissionService;

    @RequestMapping("/findAll.do")
    public ModelAndView findAll() {
        ModelAndView mv = new ModelAndView();
        List<Permission> permissions = permissionService.findAll();
        mv.addObject("permissions", permissions);
        mv.setViewName("permission-list");
        return mv;
    }

    @RequestMapping("/save.do")
    public String save(Permission permission) {
        permissionService.save(permission);
        return "redirect:/permission/findAll.do";
    }

    @RequestMapping("/deleteSelected.do")
    public String deleteSelected(String[] ids) {
        permissionService.deleteSelected(ids);
        return "redirect:/permission/findAll.do";
    }

    @RequestMapping("/deleteById.do")
    public String deleteById(String id) {
        permissionService.deleteById(id);
        return "redirect:/permission/findAll.do";
    }

    @RequestMapping("/updateById.do")
    public String updateById(String id,Model model) {
       Permission permission= permissionService.findById(id);
        model.addAttribute("permission", permission);
        return "permission-update";
    }

    @RequestMapping("/updatePermission.do")
    public String updatePermission(Permission permission) {
        permissionService.updatePermission(permission);

        return "redirect:/permission/findAll.do";
    }
}
