package com.pinyougou.task.service;

import com.pinyougou.mapper.TbSeckillGoodsMapper;
import com.pinyougou.pojo.TbSeckillGoods;
import com.pinyougou.pojo.TbSeckillGoodsExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class TaskService {
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private TbSeckillGoodsMapper seckillGoodsMapper;
    /**
     * 秒杀商品的增量更新
     * 每一分钟执行一次该任务
     */
    @Scheduled(cron = "1 * * * * ?")
    public void increatmentUpdating() {
        //查询redis中已缓存的秒杀商品id的集合
        List seckillGoodsIdList = new ArrayList<>(redisTemplate.boundHashOps("seckillGoods").keys());
        System.out.println("查询redis中已缓存的秒杀商品id的集合"+seckillGoodsIdList);
        TbSeckillGoodsExample example=new TbSeckillGoodsExample();
        TbSeckillGoodsExample.Criteria criteria = example.createCriteria();
        criteria.andStatusEqualTo("1");//已审核产品
        criteria.andStockCountGreaterThan(0);//库存数量大于零
        criteria.andStartTimeLessThanOrEqualTo(new Date());//起始时间小于等于当前时间
        criteria.andEndTimeGreaterThanOrEqualTo(new Date());//结束时间大于等于当前时间
        //增加条件,从数据库中查询出的数据的id不在redis中已存的商品中
        if (seckillGoodsIdList.size() > 0) {
            criteria.andIdNotIn(seckillGoodsIdList);
        }

        List<TbSeckillGoods> tbSeckillGoodsList = seckillGoodsMapper.selectByExample(example);
        System.out.println("数据库中查询出"+tbSeckillGoodsList);
        for (TbSeckillGoods tbSeckillGoods : tbSeckillGoodsList) {
            //将查询到的新增秒杀商品存入redis中
            redisTemplate.boundHashOps("seckillGoods").put(tbSeckillGoods.getId(), tbSeckillGoods);
        }
    }

    @Scheduled(cron = "* * * * * ?")
    public void timeoutGoodsClear() {
        //获取redis中存储的秒杀商品
        List<TbSeckillGoods> seckillGoodsList = redisTemplate.boundHashOps("seckillGoods").values();
        for (TbSeckillGoods tbSeckillGoods : seckillGoodsList) {
            //若商品的结束时间大于当前时间
            if (tbSeckillGoods.getEndTime().getTime() < new Date().getTime()) {
                //将数据同步到数据库中
                seckillGoodsMapper.updateByPrimaryKey(tbSeckillGoods);
                //从Redis中删除
                redisTemplate.boundHashOps("seckillGoods").delete(tbSeckillGoods.getId());
                System.out.println("从redis中删除" + tbSeckillGoods.getId());
            }
        }
    }

}
