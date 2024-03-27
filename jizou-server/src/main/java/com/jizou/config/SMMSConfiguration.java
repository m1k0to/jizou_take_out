package com.jizou.config;

import com.jizou.properties.SMMSProperties;
import com.jizou.utils.SMMSUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class SMMSConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public SMMSUtil smmsUtil(SMMSProperties smmsProperties) {
        log.info("创建smms工具对象: {}", smmsProperties);
        return new SMMSUtil(smmsProperties.getUrl(), smmsProperties.getToken());
    }

}
