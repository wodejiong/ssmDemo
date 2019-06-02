package com.pinyougou.page.service.impl;

import com.pinyougou.page.service.ItemPageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
@Component
public class PageListener implements MessageListener {
    @Autowired
    private ItemPageService itemPageService;
    @Override
    public void onMessage(Message message) {
        TextMessage textMessage= (TextMessage) message;
        try {
            System.out.println("监听到"+textMessage);
            String text = textMessage.getText();
            long id = Long.parseLong(text);
            itemPageService.generatePage(id);
            System.out.println("生成成功");
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
