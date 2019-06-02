package com.pinyougou.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.*;
import com.pinyougou.pojo.*;
import com.pinyougou.pojo.TbGoodsExample.Criteria;
import com.pinyougou.sellergoods.service.GoodsService;
import com.pinyougou.vo.Goods;
import entity.PageResult;
import entity.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 服务实现层
 *
 * @author Administrator
 */
@Service
@Transactional
public class GoodsServiceImpl implements GoodsService {

    @Autowired
    private TbGoodsMapper goodsMapper;
    @Autowired
    private TbGoodsDescMapper goodsDescMapper;
    @Autowired
    private TbItemCatMapper itemCatMapper;
    @Autowired
    private TbBrandMapper brandMapper;
    @Autowired
    private TbSellerMapper sellerMapper;
    @Autowired
    private TbItemMapper itemMapper;

    /**
     * 查询全部
     */
    @Override
    public List<TbGoods> findAll() {
        return goodsMapper.selectByExample(null);
    }

    /**
     * 按分页查询
     */
    @Override
    public PageResult findPage(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        Page<TbGoods> page = (Page<TbGoods>) goodsMapper.selectByExample(null);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 增加
     */
    @Override
    public void add(Goods goods) {
        TbGoods tbGoods = goods.getGoods();
        goodsMapper.insert(tbGoods);
        Long id = tbGoods.getId();
        TbGoodsDesc goodsDesc = goods.getGoodsDesc();
        goodsDesc.setGoodsId(id);
        goodsDescMapper.insert(goodsDesc);
        insertItemValues(goods, tbGoods, goodsDesc);

    }

    private void insertItemValues(Goods goods, TbGoods tbGoods, TbGoodsDesc goodsDesc) {
        //启用规格
        if ("1".equals(tbGoods.getIsEnableSpec())) {
            List<TbItem> itemList = goods.getItemList();
            for (TbItem item : itemList) {
                //设置title
                String title = tbGoods.getGoodsName();
                String spec = item.getSpec();
                Map<String, Object> object = JSON.parseObject(spec);
                for (String s : object.keySet()) {
                    title += " " + s;
                }
                item.setTitle(title);
                setItemOption(tbGoods, goodsDesc, item);
            }
        } else {
            //不启用规格
            TbItem item = new TbItem();
            //设置价格
            item.setPrice(tbGoods.getPrice());

            item.setNum(9999);

            item.setStatus("0");

            item.setIsDefault("0");

            item.setSpec("{}");

            item.setTitle(tbGoods.getGoodsName());

            setItemOption(tbGoods, goodsDesc, item);

        }
    }

    private void setItemOption(TbGoods tbGoods, TbGoodsDesc goodsDesc, TbItem item) {
        //设置图片
        String itemImages = goodsDesc.getItemImages();
        List<Map> list = JSON.parseArray(itemImages, Map.class);
        String url = (String) list.get(0).get("url");
        item.setImage(url);

        //设置categoryId
        item.setCategoryid(tbGoods.getCategory3Id());

        //创建日期与修改日期
        item.setCreateTime(new Date());
        item.setUpdateTime(new Date());

        //设置goodsID
        item.setGoodsId(tbGoods.getId());

        //设置sellerId
        item.setSellerId(tbGoods.getSellerId());

        //设置category
        TbItemCat tbItemCat = itemCatMapper.selectByPrimaryKey(tbGoods.getCategory3Id());
        item.setCategory(tbItemCat.getName());

        //设置brand
        TbBrand tbBrand = brandMapper.selectByPrimaryKey(tbGoods.getBrandId());
        item.setBrand(tbBrand.getName());
        //设置seller
        TbSeller tbSeller = sellerMapper.selectByPrimaryKey(tbGoods.getSellerId());
        item.setSeller(tbSeller.getNickName());

        itemMapper.insert(item);
    }


    /**
     * 修改
     */
    @Override
    public void update(Goods goods) {

        TbGoods goods1 = goods.getGoods();
        goodsMapper.updateByPrimaryKey(goods1);

        TbGoodsDesc goodsDesc = goods.getGoodsDesc();
        goodsDescMapper.updateByPrimaryKey(goodsDesc);

        TbItemExample example = new TbItemExample();
        TbItemExample.Criteria criteria = example.createCriteria();
        criteria.andGoodsIdEqualTo(goods1.getId());
        itemMapper.deleteByExample(example);


        insertItemValues(goods, goods.getGoods(), goodsDesc);

    }

    /**
     * 根据ID获取实体
     *
     * @param id
     * @return
     */
    @Override
    public Goods findOne(Long id) {
        Goods goods = new Goods();
        TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);
        goods.setGoods(tbGoods);

        TbGoodsDesc tbGoodsDesc = goodsDescMapper.selectByPrimaryKey(id);
        goods.setGoodsDesc(tbGoodsDesc);

        TbItemExample example = new TbItemExample();
        TbItemExample.Criteria criteria = example.createCriteria();
        criteria.andGoodsIdEqualTo(id);
        List<TbItem> tbItems = itemMapper.selectByExample(example);
        goods.setItemList(tbItems);

        return goods;
    }

    /**
     * 批量删除
     */
    @Override
    public void delete(Long[] ids) {
        for (Long id : ids) {
            goodsMapper.deleteByPrimaryKey(id);
        }
    }


    @Override
    public PageResult findPage(TbGoods goods, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);

        TbGoodsExample example = new TbGoodsExample();
        Criteria criteria = example.createCriteria();

        if (goods != null) {
            if (goods.getSellerId() != null && goods.getSellerId().length() > 0) {
                criteria.andSellerIdLike("%" + goods.getSellerId() + "%");
            }
            if (goods.getGoodsName() != null && goods.getGoodsName().length() > 0) {
                criteria.andGoodsNameLike("%" + goods.getGoodsName() + "%");
            }
            if (goods.getAuditStatus() != null && goods.getAuditStatus().length() > 0) {
                criteria.andAuditStatusLike("%" + goods.getAuditStatus() + "%");
            }
            if (goods.getIsMarketable() != null && goods.getIsMarketable().length() > 0) {
                criteria.andIsMarketableLike("%" + goods.getIsMarketable() + "%");
            }
            if (goods.getCaption() != null && goods.getCaption().length() > 0) {
                criteria.andCaptionLike("%" + goods.getCaption() + "%");
            }
            if (goods.getSmallPic() != null && goods.getSmallPic().length() > 0) {
                criteria.andSmallPicLike("%" + goods.getSmallPic() + "%");
            }
            if (goods.getIsEnableSpec() != null && goods.getIsEnableSpec().length() > 0) {
                criteria.andIsEnableSpecLike("%" + goods.getIsEnableSpec() + "%");
            }
            if (goods.getIsDelete() != null && goods.getIsDelete().length() > 0) {
                criteria.andIsDeleteLike("%" + goods.getIsDelete() + "%");
            }

        }

        Page<TbGoods> page = (Page<TbGoods>) goodsMapper.selectByExample(example);
        return new PageResult(page.getTotal(), page.getResult());
    }

    @Override
    public Result updateAuditStatus(Long[] ids, String statusNum) {

        try {
            for (long id : ids) {
                TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);
                tbGoods.setAuditStatus(statusNum);
                goodsMapper.updateByPrimaryKey(tbGoods);
            }


            return new Result(true, "修改成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "修改失败");
        }
    }


    //根据spuId查询sku
    public List<TbItem> findItemByGoodId(Long[] ids,String statusNum) {
        List<TbItem> tbItems = null;
        if (statusNum.equals("1")) {
            TbItemExample example=new TbItemExample();
            TbItemExample.Criteria criteria = example.createCriteria();
            criteria.andGoodsIdIn(Arrays.asList(ids));
             tbItems = itemMapper.selectByExample(example);
        }
        return tbItems;
    }
}
