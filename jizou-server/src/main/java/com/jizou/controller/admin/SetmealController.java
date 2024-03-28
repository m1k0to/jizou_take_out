package com.jizou.controller.admin;

import com.jizou.dto.SetmealDTO;
import com.jizou.dto.SetmealPageQueryDTO;
import com.jizou.result.PageResult;
import com.jizou.result.Result;
import com.jizou.service.SetmealService;
import com.jizou.vo.SetmealVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * 套餐管理
 */
@RestController("adminSetmealController")
@RequestMapping("/admin/setmeal")
@Slf4j
@Api(tags = "套餐相关接口")
public class SetmealController {

    @Autowired
    private SetmealService setmealService;

    /**
     * 保存套餐信息
     *
     * @param setmealDTO
     * @return
     */
    @PostMapping
    @ApiOperation(value = "保存套餐信息")
    @CacheEvict(value = "setmealCache", key = "#setmealDTO.categoryId")
    public Result save(@RequestBody SetmealDTO setmealDTO) {
        log.info("保存套餐信息: {}", setmealDTO);

        setmealService.saveWithDishes(setmealDTO);

        return Result.success();
    }

    /**
     * 批量删除菜品信息
     *
     * @param ids
     * @return
     */
    @DeleteMapping
    @ApiOperation(value = "批量删除套餐信息")
    @CacheEvict(value = "setmealCache", allEntries = true)
    public Result delete(@RequestParam List<Long> ids) {
        log.info("批量删除套餐信息: {}", ids);

        setmealService.deleteBatch(ids);

        return Result.success();
    }

    /**
     * 分页查询套餐信息
     *
     * @param setmealPageQueryDTO
     * @return
     */
    @GetMapping("/page")
    @ApiOperation(value = "分页查询套餐信息")
    public Result<PageResult> page(SetmealPageQueryDTO setmealPageQueryDTO) {

        log.info("分页查询套餐信息: {}", setmealPageQueryDTO);
        PageResult pageResult = setmealService.pageQuery(setmealPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 根据套餐id获取套餐信息
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    @ApiOperation(value = "获取套餐信息")
    public Result<SetmealVO> getById(@PathVariable Long id) {
        log.info("获取套餐信息, {}", id);
        SetmealVO setmealVO = setmealService.getById(id);
        return Result.success(setmealVO);
    }

    /**
     * 更新套餐信息
     *
     * @param setmealDTO
     * @return
     */
    @PutMapping
    @ApiOperation(value = "更新套餐信息")
    @CacheEvict(value = "setmealCache", allEntries = true)
    public Result update(@RequestBody SetmealDTO setmealDTO) {
        log.info("更新套餐信息: {}", setmealDTO);
        setmealService.updateWithDishes(setmealDTO);
        return Result.success();
    }

    /**
     * 根据id更改套餐的启售停售状态
     *
     * @param status
     * @param id
     * @return
     */
    @PostMapping("/status/{status}")
    @ApiOperation("更改套餐启停售")
    @CacheEvict(value = "setmealCache", allEntries = true)
    public Result changeStatus(@PathVariable Integer status, Long id){

        log.info("更改套餐启停售: {}", status, id);
        setmealService.changeStatus(status, id);
        return Result.success();
    }

}
