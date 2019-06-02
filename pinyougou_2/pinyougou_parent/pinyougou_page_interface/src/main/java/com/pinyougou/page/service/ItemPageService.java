package com.pinyougou.page.service;

import java.util.Map;

public interface ItemPageService {

    public boolean generatePage(Long goodsId);

    void deletePage(Long[] ids);
}
