package com.pinyougou.search.service;

import com.pinyougou.pojo.TbItem;

import java.util.List;
import java.util.Map;

public interface SearchService {
    Map<String,Object> search(Map<String,Object> searchMap);


    public void sychonizeSolr(List<TbItem> tbitems);

    public void deleteFromSolr(List ids);
}
