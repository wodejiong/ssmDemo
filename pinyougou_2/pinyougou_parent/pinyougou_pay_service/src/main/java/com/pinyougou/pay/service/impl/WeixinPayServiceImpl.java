package com.pinyougou.pay.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.wxpay.sdk.WXPay;
import com.github.wxpay.sdk.WXPayUtil;
import com.pinyougou.pay.service.WeixinPayService;
import com.pinyougou.pojo.TbPayLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import util.HttpClient;

import java.util.HashMap;
import java.util.Map;
@Service
public class WeixinPayServiceImpl implements WeixinPayService {
    @Value("${appid}")
    private String appid;
    @Value("${partner}")
    private String mchId;
    @Value("${partnerkey}")
    private String partnerkey;

    @Override
    public Map<String, String> createNative(String out_trade_no, String totalFee) {
        //准备参数
        Map<String, String> param = new HashMap<>();
        param.put("appid", appid);
        param.put("mch_id", mchId);
        param.put("nonce_str", WXPayUtil.generateNonceStr());
        param.put("body", "品优购");
        param.put("out_trade_no", out_trade_no);
        param.put("total_fee", totalFee);
        param.put("spbill_create_ip", "123.12.12.123");
        param.put("notify_url", "http://www.weixin.qq.com/wxpay/pay.php");
        param.put("trade_type", "NATIVE");
        Map<String, String> map = null;
        try {
            String xmlParam = WXPayUtil.generateSignedXml(param, partnerkey);

            //发送请求
            HttpClient httpClient = new HttpClient("https://api.mch.weixin.qq.com/pay/unifiedorder");
            httpClient.setHttps(true);
            httpClient.setXmlParam(xmlParam);
            httpClient.post();

            //处理结果
            String resultParam = httpClient.getContent();
            Map<String, String> resultMap = WXPayUtil.xmlToMap(resultParam);
            map = new HashMap<>();
            map.put("code_url", resultMap.get("code_url"));
            map.put("out_trade_no", out_trade_no);
            map.put("totalFee", (Double.valueOf(totalFee)/100)+"");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    @Override
    public Map queryOrder(String out_trade_no) {
        //准备参数
        Map<String, String> param = new HashMap<>();
        param.put("appid", appid);
        param.put("mch_id", mchId);
        param.put("out_trade_no", out_trade_no);
        param.put("nonce_str", WXPayUtil.generateNonceStr());
        try {
            String xmlParam = WXPayUtil.generateSignedXml(param, partnerkey);
            //发送请求
            HttpClient httpClient=new HttpClient("https://api.mch.weixin.qq.com/pay/orderquery");
            httpClient.setHttps(true);
            httpClient.setXmlParam(xmlParam);
            httpClient.post();

            //处理结果
            String resultString = httpClient.getContent();
            Map<String, String> resultMap = WXPayUtil.xmlToMap(resultString);
            System.out.println("返回结果集"+resultMap);
            return resultMap;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }


    @Override
    public Map<String, String> closePay(String out_trade_no) {
        //准备参数
        Map<String, String> param = new HashMap<>();
        param.put("appid", appid);
        param.put("mch_id", mchId);
        param.put("out_trade_no", out_trade_no);
        param.put("nonce_str", WXPayUtil.generateNonceStr());
        try {
            String xmlParam = WXPayUtil.generateSignedXml(param, partnerkey);
            //发送请求
            HttpClient httpClient=new HttpClient("https://api.mch.weixin.qq.com/pay/closeorder");
            httpClient.setHttps(true);
            httpClient.setXmlParam(xmlParam);
            httpClient.post();

            //处理结果
            String resultString = httpClient.getContent();
            Map<String, String> resultMap = WXPayUtil.xmlToMap(resultString);
            System.out.println("返回结果集"+resultMap);
            return resultMap;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }




    @Autowired
    private RedisTemplate redisTemplate;
    @Override
    public TbPayLog getPayLogFromRedis(String username) {
        TbPayLog payLog = (TbPayLog) redisTemplate.boundHashOps("payLog").get(username);
        return payLog;
    }
}
