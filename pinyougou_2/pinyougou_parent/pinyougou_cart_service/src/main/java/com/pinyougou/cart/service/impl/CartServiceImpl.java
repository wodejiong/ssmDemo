package com.pinyougou.cart.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.cart.service.CartService;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbOrderItem;
import com.pinyougou.vo.Cart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class CartServiceImpl implements CartService {
    @Autowired
    private TbItemMapper itemMapper;

    @Override
    public List<Cart> addGoodsToCartList(List<Cart> cartList, Long itemId, Integer num) {
        //根据itemid查询item
        TbItem item = itemMapper.selectByPrimaryKey(itemId);
        if (item == null) {
            throw new RuntimeException("商品不存在");
        }
        if (!item.getStatus().equals("1")) {
            throw new RuntimeException("商品状态不合法");
        }

        String sellerId = item.getSellerId();
        Cart cart = findCartBySellerId(cartList, sellerId);
        if (cart == null) {//如果购物车列表中没有这个seller的购物车对象
            cart = new Cart();
            cart.setSellerId(item.getSellerId());//商家id
            cart.setSellerName(item.getSeller());//商家
            List<TbOrderItem> orderItemList = new ArrayList<>();
            TbOrderItem orderItem = createOrderItem(num, item);
            orderItemList.add(orderItem);

            cart.setOrderItemList(orderItemList);//明细列表

            cartList.add(cart);

        } else {//如果购物车列表中有这个seller的购物车对象
            //获取明细列表
            List<TbOrderItem> orderItemList = cart.getOrderItemList();
            TbOrderItem orderItem = findOrderItemByItemId(orderItemList, itemId);
            if (orderItem == null) {//如果明细列表中不存在此sku
                orderItem = createOrderItem(num, item);
                orderItemList.add(orderItem);
            } else {//如果明细列表中存在此sku
                orderItem.setNum(orderItem.getNum() + num);
                orderItem.setTotalFee(new BigDecimal(item.getPrice().doubleValue() * orderItem.getNum().intValue()));

                if (orderItem.getNum() <= 0) {
                    orderItemList.remove(orderItem);
                }
                if (orderItemList.size() == 0) {
                    cartList.remove(cart);
                }
            }
        }

        return cartList;
    }

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public List<Cart> findFromRedis(String username) {
        List<Cart> cartList = (List<Cart>) redisTemplate.boundHashOps("cartList").get(username);
        if (cartList == null || cartList.size() == 0) {
            cartList = new ArrayList<>();
        }
        return cartList;
    }

    @Override
    public void addGoodsToRedis(String username, List<Cart> cartList) {
        redisTemplate.boundHashOps("cartList").put(username, cartList);
    }

    /**
     * 从明细列表中通过itemid查询明细项
     *
     * @param orderItemList
     * @param itemId
     * @return
     */
    private TbOrderItem findOrderItemByItemId(List<TbOrderItem> orderItemList, Long itemId) {
        for (TbOrderItem tbOrderItem : orderItemList) {
            if (itemId.equals(tbOrderItem.getItemId())) {
                return tbOrderItem;
            }
        }
        return null;
    }


    private TbOrderItem createOrderItem(Integer num, TbItem item) {
        TbOrderItem orderItem = new TbOrderItem();
        orderItem.setGoodsId(item.getGoodsId());
        orderItem.setItemId(item.getId());
        orderItem.setNum(num);
        orderItem.setPicPath(item.getImage());
        orderItem.setPrice(new BigDecimal(item.getPrice().doubleValue()));
        orderItem.setSellerId(item.getSellerId());
        orderItem.setTitle(item.getTitle());
        orderItem.setTotalFee(new BigDecimal(item.getPrice().doubleValue() * num.intValue()));
        return orderItem;
    }

    /**
     * 根据sellerId在购物车列表中查找购物车对象
     *
     * @param cartList
     * @param sellerId
     * @return
     */
    private Cart findCartBySellerId(List<Cart> cartList, String sellerId) {
        for (Cart cart : cartList) {
            if (sellerId.equals(cart.getSellerId())) {
                return cart;
            }
        }
        return null;
    }

    @Override
    public List<Cart> mergeCartList(List<Cart> cartList1, List<Cart> cartList2) {

        for (Cart cart : cartList1) {
            List<TbOrderItem> orderItemList = cart.getOrderItemList();
            for (TbOrderItem tbOrderItem : orderItemList) {
                cartList2 = addGoodsToCartList(cartList2, tbOrderItem.getItemId(), tbOrderItem.getNum());
            }

        }

        return cartList2;
    }
}
