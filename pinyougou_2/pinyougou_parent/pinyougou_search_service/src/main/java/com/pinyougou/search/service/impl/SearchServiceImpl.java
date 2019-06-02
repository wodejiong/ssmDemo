package com.pinyougou.search.service.impl;

import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@org.springframework.stereotype.Service
public class SearchServiceImpl implements SearchService {
    @Autowired
    private SolrTemplate solrTemplate;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public Map<String, Object> search(Map searchMap) {
        Map<String, Object> map = new HashMap<>();
        String keywords = (String) searchMap.get("keywords");
        searchMap.put("keywords", keywords.replaceAll(" ", ""));

        map.putAll(searchList(searchMap));

        List<String> categoryList = searchGroup(searchMap);
        map.put("categoryList", categoryList);

        if (searchMap.get("category") != null && !"".equals((String)searchMap.get("category"))) {
            map.putAll(searchFromRedis((String) searchMap.get("category")));
        }else{
            if (categoryList.size() > 0) {
                map.putAll(searchFromRedis(categoryList.get(0)));
            }
        }

        return map;
    }


    //高亮查询列表 : rows
    private Map<String, Object> searchList(Map searchMap) {

        Map<String, Object> map = new HashMap();

//        Query query = new SimpleQuery("*:*");
//        Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
//        query.addCriteria(criteria);
//        ScoredPage<TbItem> tbItems = solrTemplate.queryForPage(query, TbItem.class);
//        map.put("rows", tbItems.getContent());


        HighlightQuery query=new SimpleHighlightQuery();
        Criteria criteria=new Criteria("item_keywords").is(searchMap.get("keywords"));
        query.addCriteria(criteria);

        HighlightOptions highlightOptions = new HighlightOptions().addField("item_title");
        highlightOptions.setSimplePrefix("<em style='color:red'>");
        highlightOptions.setSimplePostfix("</em>");
        query.setHighlightOptions(highlightOptions);



        
        //过滤查询
        if (searchMap.get("category") != null&&!"".equals(searchMap.get("category"))) {
            Criteria filterCriteria = new Criteria("item_category").is(searchMap.get("category"));
            FilterQuery filterQuery=new SimpleFilterQuery(filterCriteria);
            query.addFilterQuery(filterQuery);
        }

        //过滤查询
        if (searchMap.get("brand") != null&&!"".equals(searchMap.get("brand"))) {
            Criteria filterCriteria = new Criteria("item_brand").is(searchMap.get("brand"));
            FilterQuery filterQuery=new SimpleFilterQuery(filterCriteria);
            query.addFilterQuery(filterQuery);
        }


        //过滤查询
        Map<String,String> spec = (Map<String, String>) searchMap.get("spec");
        if (spec != null) {
            for (String key : spec.keySet()) {
                Criteria filterCriteria = new Criteria("item_spec_"+key).is(searchMap.get(key));
                FilterQuery filterQuery=new SimpleFilterQuery(filterCriteria);
                query.addFilterQuery(filterQuery);
            }
        }

        if (searchMap.get("price") != null&&!"".equals(searchMap.get("price"))) {
            String[] prices = ((String) searchMap.get("price")).split("-");
            if (!"0".equals(prices[0])) {
                Criteria filterCriteria=new Criteria("item_price").greaterThanEqual(prices[0]);
                FilterQuery filterQuery=new SimpleFilterQuery(filterCriteria);
                query.addFilterQuery(filterQuery);
            }

            if (!"*".equals(prices[1])) {
                Criteria filterCriteria=new Criteria("item_price").lessThanEqual(prices[1]);
                FilterQuery filterQuery=new SimpleFilterQuery(filterCriteria);
                query.addFilterQuery(filterQuery);
            }
        }

        //分页
        Integer pageNo = (Integer) searchMap.get("pageNo");
        if (pageNo == null) {
            pageNo = 1;
        }
        Integer pageSize = (Integer) searchMap.get("pageSize");
        if (pageSize == null) {
            pageSize=20;
        }

        query.setOffset((pageNo - 1) * pageSize);
        query.setRows(pageSize);

        //排序
        if (searchMap.get("sort").equals("ASC")) {
            String sortFiled = (String) searchMap.get("sortField");
            Sort sort=new Sort(Sort.Direction.ASC,"item_"+sortFiled);
            query.addSort(sort);
        }

        if (searchMap.get("sort").equals("DESC")) {
            String sortFiled = (String) searchMap.get("sortField");
            Sort sort=new Sort(Sort.Direction.DESC,"item_"+sortFiled);
            query.addSort(sort);
        }



        HighlightPage<TbItem> tbItems = solrTemplate.queryForHighlightPage(query, TbItem.class);

        List list=new ArrayList();
        List<HighlightEntry<TbItem>> highlighted = tbItems.getHighlighted();
        for (HighlightEntry<TbItem> entry : highlighted) {
            TbItem item = entry.getEntity();
            List<HighlightEntry.Highlight> highlights = entry.getHighlights();
            if (highlights.size() > 0 && highlights.get(0).getSnipplets().size() > 0) {
                String s = highlights.get(0).getSnipplets().get(0);
                item.setTitle(s);
                list.add(item);
            }
        }
        map.put("totalPage", tbItems.getTotalPages());
        map.put("totalCount", tbItems.getTotalElements());
        map.put("rows", list);
        return map;
    }

    //分组查询分类  categoryList
    private List<String> searchGroup(Map SearchMap) {
        List<String> list = new ArrayList<>();

        Query query = new SimpleQuery("*:*");
        Criteria criteria=new Criteria("item_keywords").is(SearchMap.get("keywords"));
        query.addCriteria(criteria);


        GroupOptions groupOptions=new GroupOptions().addGroupByField("item_category");
        query.setGroupOptions(groupOptions);

        GroupPage<TbItem> tbItems = solrTemplate.queryForGroupPage(query, TbItem.class);

        GroupResult<TbItem> item_category = tbItems.getGroupResult("item_category");
        Page<GroupEntry<TbItem>> groupEntries = item_category.getGroupEntries();

        for (GroupEntry<TbItem> groupEntry : groupEntries) {
            String groupValue = groupEntry.getGroupValue();
            list.add(groupValue);
        }
        return list;

    }


    //从redis中查询 specList brandList
    private Map<String,Object> searchFromRedis(String category) {
        Map<String,Object> map=new HashMap<>();

        Long id = (Long) redisTemplate.boundHashOps("categoryToTypeId").get(category);

        if (id != null) {
            List<String> brandList = (List<String>) redisTemplate.boundHashOps("typeIdToBrand").get(id);
            map.put("brandList", brandList);

            List<String> specList = (List<String>) redisTemplate.boundHashOps("typeIdToSpec").get(id);
            map.put("specList", specList);
        }

        return map;

    }


    //审核完成之后,同步solr中的数据.
    public void  sychonizeSolr(List<TbItem> tbitems) {
        solrTemplate.saveBeans(tbitems);
        solrTemplate.commit();
    }

    @Override
    //从solr中删除对应的数据
    public void deleteFromSolr(List ids) {

        SolrDataQuery query = new SimpleQuery();
        Criteria criteria = new Criteria("item_goodsid").in(ids);
        query.addCriteria(criteria);
        solrTemplate.delete(query);
        solrTemplate.commit();
    }

}
