package com.jizou.service;

import com.jizou.dto.UserLoginDTO;
import com.jizou.entity.User;

public interface UserService {

    /**
     * 微信登录
     * @param userLoginDTO
     * @return
     */
    User wxLogin(UserLoginDTO userLoginDTO);


}
