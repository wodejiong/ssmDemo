package com.pinyougou.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import java.util.List;

@Component
public class SearchListener implements MessageListener {
    @Autowired
    private SearchService searchService;
    @Override
    public void onMessage(Message message) {
        TextMessage textMessage= (TextMessage) message;
        try {
            String text = textMessage.getText();
            System.out.println("捕获消息"+text);
            List<TbItem> tbItems = JSON.parseArray(text, TbItem.class);

            searchService.sychonizeSolr(tbItems);
            System.out.println("同步成功");
        } catch (JMSException e) {
            e.printStackTrace();
        }

    }
}
