package demo.wj.controller;

import com.github.pagehelper.PageInfo;
import demo.wj.domain.Product;
import demo.wj.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
@RequestMapping("/product")
public class ProductController {
    @Autowired
    private ProductService productService;

    @RequestMapping("/findAll.do")
    public ModelAndView findAll( @RequestParam(name="pageNum",defaultValue = "1")Integer pageNum,
                          @RequestParam(name="pageSize",defaultValue = "5") Integer pageSize) {
        ModelAndView mv = new ModelAndView();
        List<Product> list = productService.findAll(pageNum,pageSize);
        PageInfo pageInfo=new PageInfo(list);

        mv.addObject("pageInfo", pageInfo);
        mv.setViewName("product-list");
        return mv;
    }
    @RequestMapping("/save.do")
    public String save(Product product) {
        productService.save(product);
        return "redirect:/product/findAll.do";

    }
    @RequestMapping("/deleteSelected.do")
    public String deleteSelected(String[] ids) {
        for (String id : ids) {
            productService.deleteById(id);
        }
        return "redirect:/product/findAll.do";
    }
}
