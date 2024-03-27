package com.jizou.controller.admin;

import com.jizou.constant.MessageConstant;
import com.jizou.result.Result;
import com.jizou.utils.SMMSUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

/**
 * 通用接口
 */

@RestController
@RequestMapping("/admin/common")
@Slf4j
@Api(tags = "通用接口")
public class CommonController {

    @Autowired
    private SMMSUtil smmsUtil;

    @PostMapping("/upload")
    @ApiOperation("文件上传")
    public Result<String> upload(MultipartFile file) {
        log.info("上传文件：{}", file);

        try {
            //  原始文件名
            String originalFileName = file.getOriginalFilename();
            //  文件扩展名
            String exFileName = originalFileName.substring(originalFileName.lastIndexOf("."));
            //  构造全新文件名
            String allNewFileName = UUID.randomUUID().toString().replace("-", "") + exFileName;
            //  上传文件路径
            String filePath = smmsUtil.upload(file.getBytes(), allNewFileName);
            //  返回对应结果
            return Result.success(filePath);
        } catch (IOException e) {
            log.info("图片上传失败: {}", e.getMessage());
        }

        return Result.error(MessageConstant.UPLOAD_FAILED);
    }
}
