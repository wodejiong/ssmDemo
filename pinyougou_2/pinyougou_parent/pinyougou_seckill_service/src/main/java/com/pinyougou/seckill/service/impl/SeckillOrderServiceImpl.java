package com.pinyougou.seckill.service.impl;
import java.util.Date;
import java.util.List;

import com.pinyougou.mapper.TbSeckillGoodsMapper;
import com.pinyougou.pojo.TbSeckillGoods;
import com.pinyougou.seckill.service.SeckillOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbSeckillOrderMapper;
import com.pinyougou.pojo.TbSeckillOrder;
import com.pinyougou.pojo.TbSeckillOrderExample;
import com.pinyougou.pojo.TbSeckillOrderExample.Criteria;

import entity.PageResult;
import org.springframework.data.redis.core.RedisTemplate;
import util.IdWorker;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class SeckillOrderServiceImpl implements SeckillOrderService {

	@Autowired
	private TbSeckillOrderMapper seckillOrderMapper;
	
	/**
	 * 查询全部
	 */
	@Override
	public List<TbSeckillOrder> findAll() {
		return seckillOrderMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbSeckillOrder> page=   (Page<TbSeckillOrder>) seckillOrderMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(TbSeckillOrder seckillOrder) {
		seckillOrderMapper.insert(seckillOrder);		
	}

	
	/**
	 * 修改
	 */
	@Override
	public void update(TbSeckillOrder seckillOrder){
		seckillOrderMapper.updateByPrimaryKey(seckillOrder);
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public TbSeckillOrder findOne(Long id){
		return seckillOrderMapper.selectByPrimaryKey(id);
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			seckillOrderMapper.deleteByPrimaryKey(id);
		}		
	}




	@Override
	public PageResult findPage(TbSeckillOrder seckillOrder, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbSeckillOrderExample example=new TbSeckillOrderExample();
		Criteria criteria = example.createCriteria();
		
		if(seckillOrder!=null){			
						if(seckillOrder.getUserId()!=null && seckillOrder.getUserId().length()>0){
				criteria.andUserIdLike("%"+seckillOrder.getUserId()+"%");
			}
			if(seckillOrder.getSellerId()!=null && seckillOrder.getSellerId().length()>0){
				criteria.andSellerIdLike("%"+seckillOrder.getSellerId()+"%");
			}
			if(seckillOrder.getStatus()!=null && seckillOrder.getStatus().length()>0){
				criteria.andStatusLike("%"+seckillOrder.getStatus()+"%");
			}
			if(seckillOrder.getReceiverAddress()!=null && seckillOrder.getReceiverAddress().length()>0){
				criteria.andReceiverAddressLike("%"+seckillOrder.getReceiverAddress()+"%");
			}
			if(seckillOrder.getReceiverMobile()!=null && seckillOrder.getReceiverMobile().length()>0){
				criteria.andReceiverMobileLike("%"+seckillOrder.getReceiverMobile()+"%");
			}
			if(seckillOrder.getReceiver()!=null && seckillOrder.getReceiver().length()>0){
				criteria.andReceiverLike("%"+seckillOrder.getReceiver()+"%");
			}
			if(seckillOrder.getTransactionId()!=null && seckillOrder.getTransactionId().length()>0){
				criteria.andTransactionIdLike("%"+seckillOrder.getTransactionId()+"%");
			}
	
		}
		
		Page<TbSeckillOrder> page= (Page<TbSeckillOrder>)seckillOrderMapper.selectByExample(example);		
		return new PageResult(page.getTotal(), page.getResult());
	}
	@Autowired
	private IdWorker idWorker;
	@Autowired
	private RedisTemplate redisTemplate;
	@Autowired
	private TbSeckillGoodsMapper seckillGoodsMapper;
	/**
	 * 生成订单保存到redis中
	 * @param
	 * @param userId
	 */
	@Override
	public void saveScekillOrderToRedis( Long  skeckillGoodId,String userId) {
		TbSeckillGoods seckillGoods = (TbSeckillGoods) redisTemplate.boundHashOps("seckillGoods").get(skeckillGoodId);
		if (seckillGoods == null) {
			throw new RuntimeException("商品不存在");
		}
		if (seckillGoods.getStockCount() <= 0) {
			throw new RuntimeException("商品已被抢光");
		}
		//库存减1,
		seckillGoods.setStockCount(seckillGoods.getStockCount() - 1);
		if (seckillGoods.getStockCount() <= 0) {//若库存小于等于0,将商品同步到数据库中,删除redis中的数据
			seckillGoodsMapper.updateByPrimaryKey(seckillGoods);
			redisTemplate.boundHashOps("seckillGoods").delete(skeckillGoodId);
		}else{//若库存仍大于0,则将数据重新缓存到redis中
			redisTemplate.boundHashOps("seckillGoods").put(skeckillGoodId,seckillGoods);
		}


		TbSeckillOrder seckillOrder=new TbSeckillOrder();
		//设置属性
		seckillOrder.setId(idWorker.nextId());
		seckillOrder.setSeckillId(seckillGoods.getId());
		seckillOrder.setMoney(seckillGoods.getCostPrice());
		seckillOrder.setUserId(userId);
		seckillOrder.setSellerId(seckillGoods.getSellerId());
		seckillOrder.setCreateTime(new Date());
		seckillOrder.setStatus("0");
		//存入redis
		redisTemplate.boundHashOps("seckillOrder").put(userId, seckillOrder);
	}

	@Override
	public TbSeckillOrder getSeckillOrderFromRedis(String userId) {
		 return (TbSeckillOrder) redisTemplate.boundHashOps("seckillOrder").get(userId);
	}

	/**
	 * 订单若支付,将数据同步到数据库中
	 * @param userId
	 * @param transationId
	 */
	@Override
	public void updateOrder(String userId,String transationId) {
		TbSeckillOrder seckillOrder = (TbSeckillOrder) redisTemplate.boundHashOps("seckillOrder").get(userId);
		seckillOrder.setPayTime(new Date());
		seckillOrder.setStatus("1");
		seckillOrder.setTransactionId(transationId);
		seckillOrderMapper.insert(seckillOrder);//将数据同步到数据库中
		redisTemplate.boundHashOps("seckillOrder").delete(userId);//从redis中删除该项订单
	}

	/**
	 * 二维码超时,从redis中删除该订单,库存回退1,调用微信接口关闭订单支付
	 * @param userId
	 * @param out_trade_no
	 */
	@Override
	public void qrTimeOut(String userId, Long out_trade_no) {
		TbSeckillOrder seckillOrder = (TbSeckillOrder) redisTemplate.boundHashOps("seckillOrder").get(userId);
		if (seckillOrder != null&&seckillOrder.getId().longValue()== out_trade_no.longValue()) {
			redisTemplate.boundHashOps("seckillOrder").delete(userId);//从redis中删除该订单

			Long skeckillGoodId = seckillOrder.getSeckillId();
			TbSeckillGoods seckillGoods = (TbSeckillGoods) redisTemplate.boundHashOps("seckillGoods").get(skeckillGoodId);

			if (seckillGoods!=null&&seckillGoods.getStockCount() > 0) {//若redis中还存在该缓存
				seckillGoods.setStockCount(seckillGoods.getStockCount() + 1);//回退库存
				redisTemplate.boundHashOps("seckillGoods").put(skeckillGoodId,seckillGoods);//重新放入redis中
			}else{//redis中不存在该缓存
				seckillGoods = seckillGoodsMapper.selectByPrimaryKey(skeckillGoodId);
				seckillGoods.setStockCount(1);//回退库存
				redisTemplate.boundHashOps("seckillGoods").put(skeckillGoodId,seckillGoods);//重新放入redis中
			}

		}



	}
}
