package com.tqp.cms.config;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "momo")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MomoProperties {
    boolean enabled;
    String endpoint;
    String partnerCode;
    String accessKey;
    String secretKey;
    String redirectUrl;
    String ipnUrl;
    String requestType = "captureWallet";
    String lang = "vi";
}
