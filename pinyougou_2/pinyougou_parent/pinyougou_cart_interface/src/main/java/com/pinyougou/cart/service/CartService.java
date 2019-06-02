package com.pinyougou.cart.service;

import com.pinyougou.vo.Cart;

import java.util.List;

public interface CartService {


    public List<Cart> addGoodsToCartList(List<Cart> cartList,Long itemId,Integer num);

    public List<Cart> findFromRedis(String username);

    public void addGoodsToRedis(String username, List<Cart> cartList);

    public List<Cart> mergeCartList(List<Cart> cartList1, List<Cart> cartList2);

}
