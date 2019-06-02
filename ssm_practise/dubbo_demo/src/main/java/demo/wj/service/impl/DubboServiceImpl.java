package demo.wj.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import demo.wj.service.DubboService;
@Service
public class DubboServiceImpl implements DubboService {
    @Override
    public String showName() {
        return "wodejiong";
    }
}
