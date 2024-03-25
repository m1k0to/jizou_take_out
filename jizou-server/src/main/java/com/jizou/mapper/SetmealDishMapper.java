package com.jizou.mapper;


import com.jizou.entity.SetmealDish;
import com.jizou.vo.DishItemVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import java.util.List;
import org.apache.ibatis.annotations.Delete;

import java.util.List;

@Mapper
public interface SetmealDishMapper {

    /**
     * 根据菜品id批量查询所属套餐id
     * @param dishIds
     * @return
     */
    List<Long> getSetmealIdsByDishIds(List<Long> dishIds);

    /**
     * 根据套餐id查询套餐菜品对应信息
     * @param setmealId
     * @return
     */
    @Select("select * from setmeal_dish where setmeal_id = #{setmealId}")
    List<SetmealDish> getBySetmealId(Long setmealId);


    /**
     * 批量插入套餐和对应菜品信息
     * @param setmealDishes
     */
    void insertBatch(List<SetmealDish> setmealDishes);

    /**
     * 根据套餐id批量删除套餐菜品关联信息
     * @param setmealIds
     */
    void deleteBySetmealIds(List<Long> setmealIds);

    /**
     * 根据套餐id删除单个套餐对应的套餐菜品信息
     * @param setmealId
     */
    @Delete("delete from setmeal_dish where setmeal_id = #{setmealId}")
    void deleteBySetmealId(Long setmealId);

    @Select("select sd.name, sd.copies, d.image, d.description " +
    "from setmeal_dish sd left join dish d on sd.dish_id = d.id " +
    "where sd.setmeal_id = #{setmealId}")
    List<DishItemVO> getDishItemsBySetmealId(Long setmealId);
}
