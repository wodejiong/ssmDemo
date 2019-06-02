package com.pinyougou.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.pinyougou.cart.service.CartService;
import com.pinyougou.vo.Cart;
import entity.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import util.CookieUtil;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/cart")
public class CartController {
    @Autowired
    private HttpServletRequest request;
    @Autowired
    private HttpServletResponse response;
    @Reference
    private CartService cartService;

    @RequestMapping("/addGoodsToCartList")
    @CrossOrigin(origins = "http://localhost:9105")
    public Result addGoodsToCartList(Long itemId, Integer num) {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        try {
            List<Cart> cartList = findCartList();
            List<Cart> carts = cartService.addGoodsToCartList(cartList, itemId, num);
            if (username.equals("anonymousUser")) {//未登录
                System.out.println("cookie 存入");
                String cartListStr = JSON.toJSONString(carts);
                CookieUtil.setCookie(request, response, "cartList", cartListStr, 3600 * 24, "UTF-8");

            } else {//已登录
                System.out.println("redis 存入");
                cartService.addGoodsToRedis(username, carts);
            }

            return new Result(true, "添加成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "添加失败");
        }

    }

    @RequestMapping("/findCartList")
    public List<Cart> findCartList() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        String cartListString = CookieUtil.getCookieValue(request, "cartList", "UTF-8");
        if (cartListString == null || cartListString.equals("")) {
            cartListString = "[]";
        }
        List<Cart> cartList_cookie = JSON.parseArray(cartListString, Cart.class);
        if (username.equals("anonymousUser")) {//未登录
            System.out.println("cookie中查询");
            return cartList_cookie;
        } else {//已登录
            System.out.println("redis中查询");
            List<Cart> cartList_redis = cartService.findFromRedis(username);
            if (cartList_redis == null || cartList_redis.size() == 0) {
                cartList_redis = new ArrayList<>();
            }

            if (cartList_cookie.size() > 0) {
                List<Cart> carts = cartService.mergeCartList(cartList_redis, cartList_cookie);//合并
                cartService.addGoodsToRedis(username, carts);
                CookieUtil.deleteCookie(request, response, "cartList");
            }

            return cartList_redis;
        }

    }

}
