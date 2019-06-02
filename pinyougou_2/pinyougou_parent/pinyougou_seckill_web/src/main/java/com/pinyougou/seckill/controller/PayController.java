package com.pinyougou.seckill.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pay.service.WeixinPayService;
import com.pinyougou.pojo.TbPayLog;
import com.pinyougou.pojo.TbSeckillOrder;
import com.pinyougou.seckill.service.SeckillOrderService;
import entity.Result;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/pay")
public class PayController {
    @Reference(timeout = 10000)
    private WeixinPayService WxPayService;
    @Reference(timeout = 5000)
    private SeckillOrderService seckillOrderService;

    @RequestMapping("/createNative")
    public Map<String, String> createNative() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
//        TbPayLog payLog = WxPayService.getPayLogFromRedis(username);
        TbSeckillOrder seckillOrder = seckillOrderService.getSeckillOrderFromRedis(username);
        return WxPayService.createNative(seckillOrder.getId() + "", (long)(seckillOrder.getMoney().doubleValue()*100)+"");
    }

    @RequestMapping("/queryOrder")
    public Result queryOrder(String out_trade_no) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
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
                seckillOrderService.updateOrder(username,map.get("transaction_id"));
                break;
            }

            try {
                Thread.sleep(3000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            count++;
            System.out.println(count);
            if (count >= 100) {//二维码超时,
                result = new Result(false, "timeout");
                Map<String, String> resultMap = WxPayService.closePay(out_trade_no);
                if (!"SUCCESS".equals(resultMap.get("return_code"))) {//如果不成功
                    if ("ORDERPAID".equals(resultMap.get("err_code"))) {//错误码为已支付
                        result = new Result(true, "支付成功");
                        seckillOrderService.updateOrder(username,map.get("transaction_id"));
                    }
                }
                if (!result.getSuccess()) {
                    seckillOrderService.qrTimeOut(username,Long.valueOf(out_trade_no));//二维码超时处理方法
                }
                break;
            }

        }
        return result;
    }
}
