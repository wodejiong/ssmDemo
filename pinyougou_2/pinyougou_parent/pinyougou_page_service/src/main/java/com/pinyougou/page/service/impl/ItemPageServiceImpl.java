package com.pinyougou.page.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.mapper.TbGoodsDescMapper;
import com.pinyougou.mapper.TbGoodsMapper;
import com.pinyougou.mapper.TbItemCatMapper;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.page.service.ItemPageService;
import com.pinyougou.pojo.*;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfig;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@org.springframework.stereotype.Service
public class ItemPageServiceImpl implements ItemPageService {
    @Autowired
    private FreeMarkerConfig freeMarkerConfig;
    @Autowired
    private TbGoodsMapper goodsMapper;
    @Autowired
    private TbGoodsDescMapper goodsDescMapper;
    @Autowired
    private TbItemCatMapper itemCatMapper;
    @Autowired
    private TbItemMapper itemMapper;



    @Value("${pageDir}")
    private String pageDir;
    @Override
    public boolean generatePage(Long goodsId) {
        try {
            Configuration configuration = freeMarkerConfig.getConfiguration();
            Template template = configuration.getTemplate("item.ftl");
            Map<String,Object> map=new HashMap<>();
            TbGoods goods = goodsMapper.selectByPrimaryKey(goodsId);
            map.put("goods", goods);

            TbGoodsDesc goodsDesc = goodsDescMapper.selectByPrimaryKey(goodsId);
            map.put("goodsDesc", goodsDesc);

            TbItemCat catgory1Name = itemCatMapper.selectByPrimaryKey(goods.getCategory1Id());
            TbItemCat catgory2Name = itemCatMapper.selectByPrimaryKey(goods.getCategory2Id());
            TbItemCat catgory3Name = itemCatMapper.selectByPrimaryKey(goods.getCategory3Id());
            map.put("category1Name", catgory1Name.getName());
            map.put("category2Name", catgory2Name.getName());
            map.put("category3Name", catgory3Name.getName());

            TbItemExample example=new TbItemExample();
            TbItemExample.Criteria criteria = example.createCriteria();
            criteria.andGoodsIdEqualTo(goodsId);
            criteria.andStatusEqualTo("1");
            example.setOrderByClause("is_default desc");
            List<TbItem> tbItems = itemMapper.selectByExample(example);
            map.put("itemList", tbItems);

            Writer out=new FileWriter(pageDir+goodsId+".html");
            template.process(map,out);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    @Override
    public void deletePage(Long[] ids) {
        for (Long id : ids) {
            new File(pageDir + id + ".html").delete();
        }
    }
}
