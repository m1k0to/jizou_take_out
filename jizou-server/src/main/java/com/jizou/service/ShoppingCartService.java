package com.jizou.service;

import com.jizou.dto.ShoppingCartDTO;
import com.jizou.entity.ShoppingCart;

import java.util.List;

public interface ShoppingCartService {

    /**
     * 添加购物车
     *
     * @param shoppingCartDTO
     */
    void addShoppingCart(ShoppingCartDTO shoppingCartDTO);

    /**
     * 查看购物车商品信息
     *
     * @return
     */
    List<ShoppingCart> listShoppingCart();

    /**
     * 清空购物车商品信息
     */
    void cleanShoppingCart();

    /**
     * 减少购物车中某个商品数量
     * @param shoppingCartDTO
     */
    void subInShoppingCart(ShoppingCartDTO shoppingCartDTO);
}
