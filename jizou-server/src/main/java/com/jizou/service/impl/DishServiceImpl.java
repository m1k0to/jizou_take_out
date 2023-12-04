package com.jizou.service.impl;

import com.jizou.dto.DishDTO;
import com.jizou.entity.Dish;
import com.jizou.entity.DishFlavor;
import com.jizou.mapper.DishFlavorMapper;
import com.jizou.mapper.DishMapper;
import com.jizou.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class DishServiceImpl implements DishService {

    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private DishFlavorMapper dishFlavorMapper;

    /**
     * 新增菜品和对应口味
     *
     * @param dishDTO
     */
    @Override
    @Transactional
    public void saveWithFlavor(DishDTO dishDTO) {
        //  拷贝属性
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        //  向菜品表插入一条数据
        dishMapper.insert(dish);
        //  获取菜品id (Insert 语句生成的主键值)
        Long dishId = dish.getId();
        //  获取当前口味
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if(flavors != null && flavors.size() > 0){
            flavors.forEach(dishFlavor -> {
                dishFlavor.setDishId(dishId);
            });
            //向口味表插入n条数据
            dishFlavorMapper.insertBatch(flavors);
        }

    }
}
