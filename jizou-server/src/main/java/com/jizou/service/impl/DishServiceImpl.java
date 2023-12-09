package com.jizou.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.jizou.constant.MessageConstant;
import com.jizou.constant.StatusConstant;
import com.jizou.dto.DishDTO;
import com.jizou.dto.DishPageQueryDTO;
import com.jizou.entity.Dish;
import com.jizou.entity.DishFlavor;
import com.jizou.exception.DeletionNotAllowedException;
import com.jizou.mapper.DishFlavorMapper;
import com.jizou.mapper.DishMapper;
import com.jizou.mapper.SetmealDishMapper;
import com.jizou.result.PageResult;
import com.jizou.service.DishService;
import com.jizou.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class DishServiceImpl implements DishService {

    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private DishFlavorMapper dishFlavorMapper;
    @Autowired
    private SetmealDishMapper setMealDishMapper;
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

        if (flavors != null && flavors.size() > 0) {
            flavors.forEach(dishFlavor -> {
                dishFlavor.setDishId(dishId);
            });
            //向口味表插入n条数据
            dishFlavorMapper.insertBatch(flavors);
        }
    }

    /**
     * 菜品分页查询
     *
     * @param dishPageQueryDTO
     * @return
     */
    @Override
    public PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO) {
        PageHelper.startPage(dishPageQueryDTO.getPage(), dishPageQueryDTO.getPageSize());

        Page<DishVO> page = dishMapper.pageQuery(dishPageQueryDTO);

        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 菜品批量删除
     *
     * @param ids
     */
    @Override
    @Transactional
    public void deleteBatch(List<Long> ids) {
        for (Long id : ids) {
            //  根据id 获取对应菜品对象
            Dish dish = dishMapper.getById(id);
            //  如果菜品在售 则无法删除
            if (dish.getStatus() == StatusConstant.ENABLE) {
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }
        }

        //  判断菜品是否与套餐关联
        //  个人感觉可以写为根据一个菜品id来获取套餐id
        //  其实是为了只发一条sql
        List<Long> setMealIds = setMealDishMapper.getSetmealIdsByDIshIds(ids);
        if (setMealIds != null && setMealIds.size() > 0) {
            throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }

        //  删除菜品表中的菜品数据
        /*for(Long id:ids){
            dishMapper.deleteById(id);

            //  删除菜品关联口味数据
            dishFlavorMapper.deleteByDishId(id);
        }*/

        //  根据菜品id集合批量删除菜品数据
        dishMapper.deleteByIds(ids);

        //  根据菜品id集合批量删除口味数据
        dishFlavorMapper.deleteByDishIds(ids);
    }

    /**
     * 根据菜品id查找菜品信息
     * @param id
     * @return
     */

    @Override
    public DishVO getByIdWithFlavor(Long id) {
        //  根据id获取菜品信息
        Dish dish = dishMapper.getById(id);

        //  获取新对象并拷贝属性
        DishVO dishVO = new DishVO();
        BeanUtils.copyProperties(dish, dishVO);

        //  获取口味信息并赋值
        List<DishFlavor> flavors = dishFlavorMapper.getByDishId(id);
        dishVO.setFlavors(flavors);

        return dishVO;
    }

    /**
     * 更新菜品信息
     * @param dishDTO
     */
    @Override
    public void updateWithFlavor(DishDTO dishDTO) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);

        //  更新菜品信息
        dishMapper.update(dish);

        Long dishId = dish.getId();

        //  删除原有口味信息
        dishFlavorMapper.deleteByDishId(dishId);

        //  更新口味信息
        List<DishFlavor> dishFlavors = dishDTO.getFlavors();
        if (dishFlavors != null && dishFlavors.size() > 0) {
            dishFlavors.forEach(dishFlavor -> {
                dishFlavor.setDishId(dishId);
            });
            //向口味表插入n条数据
            dishFlavorMapper.insertBatch(dishFlavors);
        }

    }

    /**
     *
     * @param categoryId
     * @return
     */
    @Override
    public List<Dish> getByCategoryIdWithFlavor(Long categoryId) {
        return dishMapper.getByCategoryId(categoryId);
    }
}
