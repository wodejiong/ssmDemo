package com.pinyougou.solr.util;

import com.alibaba.fastjson.JSON;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbItemExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.data.solr.core.query.SolrDataQuery;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class SolrUtil {

    @Autowired
    private TbItemMapper tbItemMapper;
    @Autowired
    private SolrTemplate solrTemplate;


    public void importData() {
        TbItemExample example=new TbItemExample();
        TbItemExample.Criteria criteria = example.createCriteria();
        criteria.andStatusEqualTo("1");
        List<TbItem> tbItems = tbItemMapper.selectByExample(example);
        for (TbItem tbItem : tbItems) {
            System.out.println(tbItem);
            String spec = tbItem.getSpec();
            Map map = JSON.parseObject(spec);
            tbItem.setSpecMap(map);
        }

        solrTemplate.saveBeans(tbItems);
        solrTemplate.commit();
    }

    public void deleteAll() {
        SolrDataQuery query = new SimpleQuery("*:*");
        solrTemplate.delete(query);
        solrTemplate.commit();

    }


    public static void main(String[] args) {

        ApplicationContext applicationContext=new ClassPathXmlApplicationContext("classpath*:spring/applicationContext*.xml");
        SolrUtil solrUtil = (SolrUtil) applicationContext.getBean("solrUtil");
//        solrUtil.importData();
        solrUtil.deleteAll();

    }


}
