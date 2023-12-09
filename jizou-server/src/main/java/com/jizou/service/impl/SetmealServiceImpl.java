package com.jizou.service.impl;

import com.jizou.dto.SetmealDTO;
import com.jizou.entity.Setmeal;
import com.jizou.entity.SetmealDish;
import com.jizou.mapper.DishMapper;
import com.jizou.mapper.SetmealDishMapper;
import com.jizou.mapper.SetmealMapper;
import com.jizou.service.SetmealService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SetmealServiceImpl implements SetmealService {

    @Autowired
    private SetmealMapper setmealMapper;
    @Autowired
    private SetmealDishMapper setmealDishMapper;
    @Autowired
    private DishMapper dishMapper;
    /**
     *
     * @param setmealDTO
     */
    @Transactional
    @Override
    public void saveWithDishes(SetmealDTO setmealDTO) {

        //  套餐信息获取
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);

        //  套餐信息保存
        setmealMapper.insert(setmeal);

        //  套餐内菜品信息获取
        Long setmealId = setmeal.getId();
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();

        //  套餐内菜品信息保存
        if(setmealDishes != null && setmealDishes.size() > 0){
            setmealDishes.forEach(setmealDish -> {
                setmealDish.setSetmealId(setmealId);
            });

            setmealDishMapper.insertBatch(setmealDishes);
        }
    }
}
