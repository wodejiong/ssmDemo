package demo.wj.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import demo.wj.service.DubboService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/user")
public class DubboDemoController {
    @Reference
    private DubboService dubboService;

    @RequestMapping("/showName.do")
    @ResponseBody
    public String showName() {
        return dubboService.showName();
    }
}
