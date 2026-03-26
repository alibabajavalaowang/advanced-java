package cn.iocoder.yudao.module.family.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class FamilyModuleConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
