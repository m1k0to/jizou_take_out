package com.jizou.controller.admin;

import com.jizou.constant.MessageConstant;
import com.jizou.result.Result;
import com.jizou.utils.AliOssUtil;
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
    private AliOssUtil aliOssUtil;

    @PostMapping("/upload")
    @ApiOperation("文件上传")
    public Result<String> upload(MultipartFile file) {
        log.info("上传文件：{}", file);

        /*try {
            //  原始文件名
            String originalFileName = file.getOriginalFilename();
            //  文件扩展名
            String exFileName = originalFileName.substring(originalFileName.lastIndexOf("."));
            //  构造全新文件名
            String allNewFileName = UUID.randomUUID().toString() + exFileName;
            //  上传文件路径
            String filePath = aliOssUtil.upload(file.getBytes(), allNewFileName);

            //  返回对应结果
            return Result.success(filePath);
        } catch (IOException e) {
            log.info("文件上传失败: {}", e);
        }

        return Result.error(MessageConstant.UPLOAD_FAILED);*/

        return Result.success("https://img-blog.csdnimg.cn/img_convert/ab0ed81f069fc08e241b794fb027c051.jpeg");
    }
}
