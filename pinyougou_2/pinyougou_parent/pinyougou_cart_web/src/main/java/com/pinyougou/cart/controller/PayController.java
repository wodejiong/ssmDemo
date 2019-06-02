package com.pinyougou.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.order.service.OrderService;
import com.pinyougou.pay.service.WeixinPayService;
import com.pinyougou.pojo.TbPayLog;
import entity.Result;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import util.IdWorker;

import java.util.Map;

@RestController
@RequestMapping("/pay")
public class PayController {
    @Reference(timeout = 10000)
    private WeixinPayService WxPayService;
    @Reference
    private OrderService orderService;

    @RequestMapping("/createNative")
    public Map<String, String> createNative() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        TbPayLog payLog = WxPayService.getPayLogFromRedis(username);
        return WxPayService.createNative(payLog.getOutTradeNo(),  "1");
    }

    @RequestMapping("/queryOrder")
    public Result queryOrder(String out_trade_no) {
        Result result = null;
        int count=0;
        //轮询查询
        while (true) {
            Map<String,String> map = WxPayService.queryOrder(out_trade_no);
            if (map == null) {
                result = new Result(false, "支付失败");
                break;
            }
            if (map.get("trade_state").equals("SUCCESS")) {
                result = new Result(true, "支付成功");
                orderService.updatePayLogStatus(out_trade_no, map.get("transaction_id"));
                break;
            }


            try {
                Thread.sleep(3000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            count++;
            if (count >= 3) {
                result = new Result(false, "timeout");
                break;
            }

        }
        return result;
    }
}
