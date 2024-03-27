package com.jizou.utils;

import cn.hutool.http.Header;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson.JSONObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

@Data
@AllArgsConstructor
@Slf4j
public class SMMSUtil {

    private String url;
    private String token;

    public String upload(byte[] bytes, String newFileName) {

        FileOutputStream outputStream = null;
        File file = new File(newFileName);

        try{
            if(!file.exists()){
                file.createNewFile();
                outputStream = new FileOutputStream(file.getAbsoluteFile());
                outputStream.write(bytes);
            }
        }catch(IOException e){
            throw new RuntimeException(e);
        }finally {
            try{
                if(outputStream != null){
                    outputStream.close();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        HttpResponse response = HttpRequest.post(url)
                .header(Header.CONTENT_TYPE, "multipart/form-data")
                .header(Header.AUTHORIZATION, token)
                .header(Header.USER_AGENT, "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/123.0.0.0 Safari/537.36")
                .form("smfile", file)
                .execute();

        System.out.println(file.delete());

        if(outputStream != null){
            try {
                outputStream.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        JSONObject result = JSONObject.parseObject(response.body());
        //  TODO 增加图片重复上传逻辑

        String imageUrl = result.getJSONObject("data").getString("url");

        return imageUrl;
    }

        //  TODO 增加图片删除逻辑
}
