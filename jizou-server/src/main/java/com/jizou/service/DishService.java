package com.jizou.service;

import com.jizou.dto.DishDTO;

public interface DishService {

    /**
     * 新增菜品和对应口味
     * @param dishDTO
     */
    void saveWithFlavor(DishDTO dishDTO);
}
