package com.jizou.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Data
@ConfigurationProperties(prefix = "jizou.smms")
public class SMMSProperties {
    private String url;
    private String token;
}
