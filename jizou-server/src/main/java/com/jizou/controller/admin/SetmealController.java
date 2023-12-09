package com.jizou.controller.admin;

import com.jizou.dto.SetmealDTO;
import com.jizou.result.Result;
import com.jizou.service.SetmealService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * 套餐管理
 */
@RestController
@RequestMapping("/admin/setmeal")
@Slf4j
@Api(tags = "套餐相关接口")
public class SetmealController {

    @Autowired
    private SetmealService setmealService;

    /**
     * 保存套餐信息
     * @param setmealDTO
     * @return
     */
    @PostMapping
    @ApiOperation(value = "保存套餐信息")
    public Result save(@RequestBody SetmealDTO setmealDTO){
        log.info("保存套餐信息: {}", setmealDTO);

        setmealService.saveWithDishes(setmealDTO);

        return Result.success();
    }

}
