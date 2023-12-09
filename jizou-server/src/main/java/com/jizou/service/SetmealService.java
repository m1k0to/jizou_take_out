package com.jizou.service;


import com.jizou.dto.SetmealDTO;
import com.jizou.dto.SetmealPageQueryDTO;
import com.jizou.result.PageResult;
import com.jizou.vo.SetmealVO;

import java.util.List;

public interface SetmealService {


    /**
     * 保存套餐信息
     * @param setmealDTO
     */
    void saveWithDishes(SetmealDTO setmealDTO);

    /**
     * 分页查询菜品信息
     * @param setmealPageQueryDTO
     * @return
     */
    PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO);

    /**
     * 批量删除套餐信息
     * @param ids
     */
    void deleteBatch(List<Long> ids);

    /**
     * 获取套餐信息
     * @param id
     * @return
     */
    SetmealVO getById(Long id);

    /**
     * 更新套餐信息
     * @param setmealDTO
     */
    void updateWithDishes(SetmealDTO setmealDTO);

    /**
     * 更改套餐启停售
     * @param status
     * @param id
     */
    void changeStatus(Integer status, Long id);
}
