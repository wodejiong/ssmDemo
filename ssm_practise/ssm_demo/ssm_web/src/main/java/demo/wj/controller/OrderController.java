package demo.wj.controller;

import com.github.pagehelper.PageInfo;
import demo.wj.domain.Orders;
import demo.wj.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
@RequestMapping("/orders")
public class OrderController {
    @Autowired
    private OrderService orderService;


//    @RequestMapping("/findAll.do")
//    public String findAll(Model model) {
//        List<Orders> list = orderService.findAll();
//        model.addAttribute("ordersList", list);
//        return "orders-list";
//    }

    @RequestMapping("/findAll.do")
    public ModelAndView findAll(@RequestParam(defaultValue = "1",name = "currentPage")Integer currentPage,@RequestParam(defaultValue = "5",name = "size")Integer size) {
        ModelAndView mv=new ModelAndView();
        List<Orders> list = orderService.findAll(currentPage,size);
        PageInfo pageInfo = new PageInfo(list);
        mv.addObject("pageInfo", pageInfo);
        mv.setViewName("orders-list");
        return mv;
    }
    @RequestMapping("/findById.do")
    public String findById(String id,Model model) {
        Orders orders=orderService.findById(id);
        model.addAttribute("orders", orders);
        return "orders-show";
    }

}
