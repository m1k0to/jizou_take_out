package com.jizou.controller.user;

import com.jizou.dto.ShoppingCartDTO;
import com.jizou.entity.ShoppingCart;
import com.jizou.result.Result;
import com.jizou.service.ShoppingCartService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/shoppingCart")
@Slf4j
@Api(tags = "小程序购物车相关接口")
public class ShoppingCartController {

    @Autowired
    private ShoppingCartService shoppingCartService;

    /**
     * 添加购物车
     * @param shoppingCartDTO
     * @return
     */
    @PostMapping("/add")
    @ApiOperation("添加商品信息至购物车")
    public Result addToShoppingCart(@RequestBody ShoppingCartDTO shoppingCartDTO){

        log.info("添加购物车， 商品信息为: {}", shoppingCartDTO);
        shoppingCartService.addShoppingCart(shoppingCartDTO);

        return Result.success();
    }

    /**
     * 查看购物车商品信息
     *
     * @return
     */
    @GetMapping("/list")
    @ApiOperation("查看购物车商品信息")
    public Result<List<ShoppingCart>> listShoppingCart(){
       List<ShoppingCart> list = shoppingCartService.listShoppingCart();
       return Result.success(list);
    }

    /**
     * 清空当前用户购物车物品信息
     * @return
     */
    @DeleteMapping("/clean")
    @ApiOperation("清空购物车信息")
    public Result cleanShoppingCart(){
        shoppingCartService.cleanShoppingCart();
        return Result.success();
    }

    @PostMapping("/sub")
    @ApiOperation("减少购物车中的某个物品数量")
    public Result subInShoppingCart(@RequestBody ShoppingCartDTO shoppingCartDTO){
        shoppingCartService.subInShoppingCart(shoppingCartDTO);
        return Result.success();
    }

}
