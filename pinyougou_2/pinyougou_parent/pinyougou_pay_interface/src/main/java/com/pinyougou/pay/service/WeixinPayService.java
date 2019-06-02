package com.pinyougou.pay.service;

import com.pinyougou.pojo.TbPayLog;

import java.util.Map;

public interface WeixinPayService {



    public Map createNative(String out_trade_no,String totalFee);

    public Map queryOrder(String out_trade_no);

    public Map<String, String> closePay(String out_trade_no);

    /**
     * 从redis中获取payLog对象
     * @param username 用户名
     * @return
     */
    public TbPayLog getPayLogFromRedis(String username);
}
