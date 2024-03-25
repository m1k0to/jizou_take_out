package com.jizou.controller.user;

import com.jizou.constant.JwtClaimsConstant;
import com.jizou.dto.UserLoginDTO;
import com.jizou.entity.User;
import com.jizou.properties.JwtProperties;
import com.jizou.result.Result;
import com.jizou.service.UserService;
import com.jizou.utils.JwtUtil;
import com.jizou.vo.UserLoginVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/user/user")
@Api(tags = "小程序用户相关接口")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private JwtProperties jwtProperties;

    @PostMapping("/login")
    @ApiOperation("微信登录")
    public Result<UserLoginVO> login(@RequestBody UserLoginDTO userLoginDTO){
        log.info("微信用户登录: {}", userLoginDTO.getCode());

        //  微信登录
        User loginedUser = userService.wxLogin(userLoginDTO);

        //  微信用户jwt令牌生成
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.USER_ID, loginedUser.getId());
        String token = JwtUtil.createJWT(jwtProperties.getUserSecretKey(), jwtProperties.getUserTtl(), claims);

        //  创建UserLoginVO对象
        UserLoginVO userLoginVO = UserLoginVO.builder()
                .id(loginedUser.getId())
                .openid(loginedUser.getOpenid())
                .token(token)
                .build();

        return Result.success(userLoginVO);
    }

}
