package com.jizou.mapper;

import com.github.pagehelper.Page;
import com.jizou.annotation.AutoFill;
import com.jizou.dto.SetmealPageQueryDTO;
import com.jizou.entity.Setmeal;
import com.jizou.enumeration.OperationType;
import com.jizou.vo.DishItemVO;
import com.jizou.vo.SetmealVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface SetmealMapper {

    /**
     * 根据分类id查询套餐的数量
     *
     * @param id
     * @return
     */
    @Select("select count(id) from setmeal where category_id = #{categoryId}")
    Integer countByCategoryId(Long id);

    /**
     * 保存套餐信息
     *
     * @param setmeal
     */
    @AutoFill(OperationType.INSERT)
    void insert(Setmeal setmeal);

    /**
     * 分页查询套餐信息
     *
     * @param setmealPageQueryDTO
     * @return
     */
    Page<SetmealVO> pageQuery(SetmealPageQueryDTO setmealPageQueryDTO);


    /**
     * 根据id查找套餐信息
     *
     * @param id
     * @return
     */
    @Select("select * from setmeal where id = #{id}")
    Setmeal getById(Long id);

    /**
     * 批量删除套餐信息
     *
     * @param ids
     */
    void deleteByIds(List<Long> ids);


    /**
     * 更新套餐信息
     *
     * @param setmeal
     */
    @AutoFill(OperationType.UPDATE)
    void update(Setmeal setmeal);

    /**
     * 动态查询
     *
     * @param setmeal
     * @return
     */
    List<Setmeal> list(Setmeal setmeal);

    /**
     * 根据条件查询符合条件套餐的数量
     *
     * @param map
     * @return
     */
    Integer setmealsConditionalQuery(Map map);
}
