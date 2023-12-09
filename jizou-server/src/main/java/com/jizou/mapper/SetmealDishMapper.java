package com.jizou.mapper;

import com.jizou.annotation.AutoFill;
import com.jizou.entity.SetmealDish;
import com.jizou.enumeration.OperationType;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SetmealDishMapper {

    /**
     * 根据菜品id查询对应套餐id
     * @param dishIds
     * @return
     */
    List<Long> getSetmealIdsByDIshIds(List<Long> dishIds);

    /**
     * 批量插入套餐和对应菜品信息
     * @param setmealDishes
     */
    void insertBatch(List<SetmealDish> setmealDishes);
}
