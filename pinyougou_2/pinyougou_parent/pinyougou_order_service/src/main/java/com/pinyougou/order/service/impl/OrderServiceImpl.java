package com.pinyougou.order.service.impl;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.pinyougou.mapper.TbOrderItemMapper;
import com.pinyougou.mapper.TbPayLogMapper;
import com.pinyougou.order.service.OrderService;
import com.pinyougou.pojo.TbOrderItem;
import com.pinyougou.pojo.TbPayLog;
import com.pinyougou.vo.Cart;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbOrderMapper;
import com.pinyougou.pojo.TbOrder;
import com.pinyougou.pojo.TbOrderExample;
import com.pinyougou.pojo.TbOrderExample.Criteria;

import entity.PageResult;
import org.springframework.data.redis.core.RedisTemplate;
import util.IdWorker;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class OrderServiceImpl implements OrderService {

	@Autowired
	private TbOrderMapper orderMapper;
	
	/**
	 * 查询全部
	 */
	@Override
	public List<TbOrder> findAll() {
		return orderMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbOrder> page=   (Page<TbOrder>) orderMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}
	@Autowired
	private RedisTemplate redisTemplate;
	@Autowired
	private IdWorker idWorker;
	@Autowired
	private TbOrderItemMapper tbOrderItemMapper;
	@Autowired
	private TbPayLogMapper payLogMapper;
	/**
	 * 增加
	 */
	@Override
	public void add(TbOrder order) {
		//根据order在redis中查询cartList
		List<Cart> cartList = (List<Cart>) redisTemplate.boundHashOps("cartList").get(order.getUserId());
		//整张订单的总金额
		double orderMoney = 0;
		//整张订单的订单号列表
		List<String> orderList=new ArrayList<>();
		//将cartList插入数据库
		for (Cart cart : cartList) {
			TbOrder tbOrder=new TbOrder();
			long orderId = idWorker.nextId();
			tbOrder.setOrderId(orderId);//orderId
			tbOrder.setPaymentType(order.getPaymentType());//支付方式
			tbOrder.setStatus("1");//状态
			tbOrder.setCreateTime(new Date());//创建时间
			tbOrder.setUpdateTime(new Date());//更新时间
			tbOrder.setUserId(order.getUserId());//用户姓名
			tbOrder.setReceiverAreaName(order.getReceiverAreaName());//用户地址
			tbOrder.setReceiverMobile(order.getReceiverMobile());//用户电话
			tbOrder.setSourceType(order.getSourceType());//来源类型
			tbOrder.setSellerId(cart.getSellerId());
			List<TbOrderItem> orderItemList = cart.getOrderItemList();
			double totalFee=0;
			for (TbOrderItem tbOrderItem : orderItemList) {
				tbOrderItem.setId(idWorker.nextId());
				tbOrderItem.setOrderId(orderId);
				tbOrderItem.setSellerId(cart.getSellerId());
				tbOrderItemMapper.insert(tbOrderItem);
				totalFee+=tbOrderItem.getTotalFee().doubleValue();
			}


			orderMoney += totalFee;
			orderList.add(orderId + "");
			tbOrder.setPayment(new BigDecimal(totalFee));//金额
			orderMapper.insert(tbOrder);
		}
		//清除此用户购物车缓存
		redisTemplate.boundHashOps("cartList").delete(order.getUserId());

		if ("1".equals(order.getPaymentType())) {
			//向支付日志表中插入数据
			TbPayLog payLog=new TbPayLog();
			payLog.setOutTradeNo(idWorker.nextId() + "");//支付账单号
			payLog.setCreateTime(new Date());//创建日期
			payLog.setTotalFee((long)orderMoney);//总金额
			payLog.setUserId(order.getUserId());//用户姓名
			payLog.setTradeState("0");//支付状态
			//订单列表
			payLog.setOrderList(orderList.toString().replace("[", "").replace("]", ""));
			payLog.setPayType("1");
			//插入数据库
			payLogMapper.insert(payLog);
			//保存到redis中
			redisTemplate.boundHashOps("payLog").put(order.getUserId(), payLog);
		}


	}

	
	/**
	 * 修改
	 */
	@Override
	public void update(TbOrder order){
		orderMapper.updateByPrimaryKey(order);
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public TbOrder findOne(Long id){
		return orderMapper.selectByPrimaryKey(id);
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			orderMapper.deleteByPrimaryKey(id);
		}		
	}
	
	
		@Override
	public PageResult findPage(TbOrder order, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbOrderExample example=new TbOrderExample();
		Criteria criteria = example.createCriteria();
		
		if(order!=null){			
						if(order.getPaymentType()!=null && order.getPaymentType().length()>0){
				criteria.andPaymentTypeLike("%"+order.getPaymentType()+"%");
			}
			if(order.getPostFee()!=null && order.getPostFee().length()>0){
				criteria.andPostFeeLike("%"+order.getPostFee()+"%");
			}
			if(order.getStatus()!=null && order.getStatus().length()>0){
				criteria.andStatusLike("%"+order.getStatus()+"%");
			}
			if(order.getShippingName()!=null && order.getShippingName().length()>0){
				criteria.andShippingNameLike("%"+order.getShippingName()+"%");
			}
			if(order.getShippingCode()!=null && order.getShippingCode().length()>0){
				criteria.andShippingCodeLike("%"+order.getShippingCode()+"%");
			}
			if(order.getUserId()!=null && order.getUserId().length()>0){
				criteria.andUserIdLike("%"+order.getUserId()+"%");
			}
			if(order.getBuyerMessage()!=null && order.getBuyerMessage().length()>0){
				criteria.andBuyerMessageLike("%"+order.getBuyerMessage()+"%");
			}
			if(order.getBuyerNick()!=null && order.getBuyerNick().length()>0){
				criteria.andBuyerNickLike("%"+order.getBuyerNick()+"%");
			}
			if(order.getBuyerRate()!=null && order.getBuyerRate().length()>0){
				criteria.andBuyerRateLike("%"+order.getBuyerRate()+"%");
			}
			if(order.getReceiverAreaName()!=null && order.getReceiverAreaName().length()>0){
				criteria.andReceiverAreaNameLike("%"+order.getReceiverAreaName()+"%");
			}
			if(order.getReceiverMobile()!=null && order.getReceiverMobile().length()>0){
				criteria.andReceiverMobileLike("%"+order.getReceiverMobile()+"%");
			}
			if(order.getReceiverZipCode()!=null && order.getReceiverZipCode().length()>0){
				criteria.andReceiverZipCodeLike("%"+order.getReceiverZipCode()+"%");
			}
			if(order.getReceiver()!=null && order.getReceiver().length()>0){
				criteria.andReceiverLike("%"+order.getReceiver()+"%");
			}
			if(order.getInvoiceType()!=null && order.getInvoiceType().length()>0){
				criteria.andInvoiceTypeLike("%"+order.getInvoiceType()+"%");
			}
			if(order.getSourceType()!=null && order.getSourceType().length()>0){
				criteria.andSourceTypeLike("%"+order.getSourceType()+"%");
			}
			if(order.getSellerId()!=null && order.getSellerId().length()>0){
				criteria.andSellerIdLike("%"+order.getSellerId()+"%");
			}
	
		}
		
		Page<TbOrder> page= (Page<TbOrder>)orderMapper.selectByExample(example);		
		return new PageResult(page.getTotal(), page.getResult());
	}

	@Override
	public void updatePayLogStatus(String out_trade_no, String transaction_id) {
		TbPayLog payLog = payLogMapper.selectByPrimaryKey(out_trade_no);
		payLog.setPayTime(new Date());
		payLog.setTradeState("1");
		payLog.setTransactionId(transaction_id);
		payLogMapper.updateByPrimaryKey(payLog);

		String orderList = payLog.getOrderList();
		String[] orderIds = orderList.split(",");
		for (String orderId : orderIds) {
			TbOrder tbOrder = orderMapper.selectByPrimaryKey(Long.valueOf(orderId));
			tbOrder.setPaymentTime(new Date());
			tbOrder.setStatus("2");
		}

		redisTemplate.boundHashOps("payLog").delete(payLog.getUserId());
	}
}
