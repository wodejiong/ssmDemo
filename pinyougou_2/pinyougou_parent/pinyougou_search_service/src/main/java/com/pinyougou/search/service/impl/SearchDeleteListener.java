package com.pinyougou.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jms.*;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

@Component
public class SearchDeleteListener implements MessageListener {
    @Autowired
    private SearchService searchService;

    @Override
    public void onMessage(Message message) {
        ObjectMessage objectMessage= (ObjectMessage) message;
        try {
            Long[] ids = (Long[]) objectMessage.getObject();
            searchService.deleteFromSolr(Arrays.asList(ids));
        } catch (JMSException e) {
            e.printStackTrace();
        }

    }
}