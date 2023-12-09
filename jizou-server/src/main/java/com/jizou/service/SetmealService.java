package com.jizou.service;


import com.jizou.dto.SetmealDTO;

public interface SetmealService {


    /**
     * 保存套餐信息
     * @param setmealDTO
     */
    void saveWithDishes(SetmealDTO setmealDTO);
}
