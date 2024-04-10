package com.jizou.mapper;

import com.jizou.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.Map;

@Mapper
public interface UserMapper {

    /**
     * 根据openId查询用户
     * @param openid
     * @return
     */
    @Select("select * from user where openid = #{openid}")
    User getByOpenid(String openid);

    /**
     * 插入用户数据
     * @param user
     */
    void insert(User user);

    @Select("select * from user where id = #{userId}")
    User getById(Long userId);

    /**
     * 根据条件动态统计总用户/当日新增用户数量
     * @param map
     * @return
     */
    Integer usersOfTheDay(Map map);
}
