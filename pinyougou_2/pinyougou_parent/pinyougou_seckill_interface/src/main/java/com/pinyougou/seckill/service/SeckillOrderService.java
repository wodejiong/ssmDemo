package com.pinyougou.seckill.service;
import java.util.List;

import com.pinyougou.pojo.TbSeckillGoods;
import com.pinyougou.pojo.TbSeckillOrder;

import entity.PageResult;
/**
 * 服务层接口
 * @author Administrator
 *
 */
public interface SeckillOrderService {

	/**
	 * 返回全部列表
	 * @return
	 */
	public List<TbSeckillOrder> findAll();
	
	
	/**
	 * 返回分页列表
	 * @return
	 */
	public PageResult findPage(int pageNum, int pageSize);
	
	
	/**
	 * 增加
	*/
	public void add(TbSeckillOrder seckillOrder);
	
	
	/**
	 * 修改
	 */
	public void update(TbSeckillOrder seckillOrder);
	

	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	public TbSeckillOrder findOne(Long id);
	
	
	/**
	 * 批量删除
	 * @param ids
	 */
	public void delete(Long[] ids);

	/**
	 * 分页
	 * @param pageNum 当前页 码
	 * @param pageSize 每页记录数
	 * @return
	 */
	public PageResult findPage(TbSeckillOrder seckillOrder, int pageNum, int pageSize);

	/**
	 * 将订单保存到redis中
	 */
	public void saveScekillOrderToRedis(Long  skeckillGoodId,String userId);

	/**
	 * 通过用户名从redis中查询秒杀订单对象
	 * @param userId
	 * @return
	 */
	public TbSeckillOrder getSeckillOrderFromRedis(String userId);

	void updateOrder(String userId,String transationId);

	/**
	 * 二维码超时,从redis中删除该订单,库存回退1,调用微信接口关闭订单支付
	 * @param userId
	 * @param out_trade_no
	 */
	public void qrTimeOut(String userId,Long out_trade_no);
}
