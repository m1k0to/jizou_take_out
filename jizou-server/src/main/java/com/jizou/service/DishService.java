package com.jizou.service;

import com.jizou.dto.DishDTO;
import com.jizou.dto.DishPageQueryDTO;
import com.jizou.result.PageResult;
import com.jizou.vo.DishVO;

import java.util.List;

public interface DishService {

    /**
     * 新增菜品和对应口味
     * @param dishDTO
     */
    void saveWithFlavor(DishDTO dishDTO);

    /**
     * 菜品分页查询
     * @param dishPageQueryDTO
     * @return
     */
    PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO);

    /**
     * 菜品批量删除
     * @param ids
     */
    void deleteBatch(List<Long> ids);

    /**
     * 根据菜品id查找菜品信息和对应口味信息
     * @param id
     * @return
     */
    DishVO getByIdWithFlavor(Long id);

    /**
     * 更新菜品信息
     * @param dishDTO
     */
    void updateWithFlavor(DishDTO dishDTO);
}
