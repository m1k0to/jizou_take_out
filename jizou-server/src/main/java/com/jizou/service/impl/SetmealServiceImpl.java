package com.jizou.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.jizou.constant.MessageConstant;
import com.jizou.constant.StatusConstant;
import com.jizou.dto.SetmealDTO;
import com.jizou.dto.SetmealPageQueryDTO;
import com.jizou.entity.Setmeal;
import com.jizou.entity.SetmealDish;
import com.jizou.exception.DeletionNotAllowedException;
import com.jizou.mapper.DishMapper;
import com.jizou.mapper.SetmealDishMapper;
import com.jizou.mapper.SetmealMapper;
import com.jizou.result.PageResult;
import com.jizou.service.SetmealService;
import com.jizou.vo.SetmealVO;
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

    /**
     * 分页查询菜品信息
     * @param setmealPageQueryDTO
     * @return
     */
    @Override
    public PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO) {

        PageHelper.startPage(setmealPageQueryDTO.getPage(), setmealPageQueryDTO.getPageSize());

        Page<SetmealVO> page = setmealMapper.pageQuery(setmealPageQueryDTO);

        Long total = page.getTotal();
        List<SetmealVO> setmealList = page.getResult();

        return new PageResult(total, setmealList);
    }

    /**
     * 批量删除套餐信息
     * @param ids
     */
    @Override
    public void deleteBatch(List<Long> ids) {
        //  判断菜品是否启售
        for (Long id : ids) {
            Setmeal setmeal = setmealMapper.getById(id);
            if(setmeal.getStatus() == StatusConstant.ENABLE){
                throw new DeletionNotAllowedException(MessageConstant.SETMEAL_ON_SALE);
            }
        }

        //  删除套餐
        setmealMapper.deleteByIds(ids);

        //  删除套餐菜品关联信息
        setmealDishMapper.deleteBySetmealIds(ids);
    }

    /**
     * 获取套餐信息
     * @param id
     * @return
     */
    @Override
    public SetmealVO getById(Long id) {
        //  获取套餐信息
        Setmeal setmeal = setmealMapper.getById(id);
        SetmealVO setmealVO = new SetmealVO();
        BeanUtils.copyProperties(setmeal, setmealVO);

        //  获取套餐拥有菜品信息
        List<SetmealDish> setmealDishes = setmealDishMapper.getBySetmealId(id);
        setmealVO.setSetmealDishes(setmealDishes);

        return setmealVO;
    }

    @Override
    public void updateWithDishes(SetmealDTO setmealDTO) {
        //  套餐信息获取
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);

        //  套餐信息保存
        setmealMapper.update(setmeal);

        //  删除原有套餐菜品对应信息
        setmealDishMapper.deleteBySetmealId(setmeal.getId());

        //  套餐菜品信息获取及保存
        Long setmealId = setmealDTO.getId();
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        if(setmealDishes != null && setmealDishes.size() > 0){
            setmealDishes.forEach(setmealDish -> {
                setmealDish.setSetmealId(setmealId);
            });
            setmealDishMapper.insertBatch(setmealDishes);
        }

    }

    /**
     * 更改套餐启停售
     * @param status
     * @param id
     */
    @Override
    public void changeStatus(Integer status, Long id) {
        Setmeal setmeal = Setmeal.builder()
                .id(id)
                .status(status)
                .build();
        setmealMapper.update(setmeal);
    }
}
