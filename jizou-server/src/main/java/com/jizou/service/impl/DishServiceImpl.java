package com.jizou.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.jizou.constant.MessageConstant;
import com.jizou.constant.StatusConstant;
import com.jizou.dto.DishDTO;
import com.jizou.dto.DishPageQueryDTO;
import com.jizou.entity.Dish;
import com.jizou.entity.DishFlavor;
import com.jizou.entity.Setmeal;
import com.jizou.exception.DeletionNotAllowedException;
import com.jizou.mapper.DishFlavorMapper;
import com.jizou.mapper.DishMapper;
import com.jizou.mapper.SetmealDishMapper;
import com.jizou.mapper.SetmealMapper;
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
    @Autowired
    private SetmealMapper setmealMapper;
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
        List<Long> setMealIds = setMealDishMapper.getSetmealIdsByDishIds(ids);
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
     * 根据套餐id查询菜品和对应口味
     * @param categoryId
     * @return
     */
    @Override
    public List<DishVO> listWithFlavor(Long categoryId) {
        //  条件查询
        Dish dish = Dish.builder()
                .categoryId(categoryId)
                .status(StatusConstant.ENABLE)
                .build();

        List<Dish> dishList = dishMapper.list(dish);
        List<DishVO> dishVOList = new ArrayList<>();

        for(Dish d : dishList){
            //  获取视图对象并复制属性
            DishVO dishVO = new DishVO();
            BeanUtils.copyProperties(d, dishVO);

            //  根据id获取口味
            List<DishFlavor> flavors = dishFlavorMapper.getByDishId(d.getId());
            dishVO.setFlavors(flavors);
            dishVOList.add(dishVO);
        }

        return dishVOList;
    }

    /**
     * 根据分类id查找菜品信息
     * @param categoryId
     * @return
     */
    @Override
    public List<Dish> getByCategoryId(Long categoryId) {
        Dish dish = Dish.builder()
                .categoryId(categoryId)
                .build();
        return dishMapper.list(dish);
    }

    /**
     * 更改菜品启停售
     * @param status
     * @param id
     */
    @Override
    public void changeStatus(Integer status, Long id) {
        Dish dish = Dish.builder()
                .id(id)
                .status(status)
                .build();

        dishMapper.update(dish);

        if(status == StatusConstant.DISABLE){
            //  停售时还需要将包含该菜品的套餐停售
            List<Long> dishIds = new ArrayList<>();
            dishIds.add(id);

            //  查询套餐Id
            List<Long> setmealIds = setMealDishMapper.getSetmealIdsByDishIds(dishIds);

            //  依次修改套餐信息
            if(setmealIds != null && setmealIds.size() > 0){
                for(Long setmealId : setmealIds){
                    Setmeal setmeal = Setmeal.builder()
                            .id(setmealId)
                            .status(StatusConstant.DISABLE)
                            .build();
                    setmealMapper.update(setmeal);
                }
            }
        }
    }
}
