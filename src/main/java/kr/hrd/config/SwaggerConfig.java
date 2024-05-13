package kr.hrd.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI openAPI(){
        return new OpenAPI()
                .components(new Components())
                .info(new Info());
    }

    private Info appInfo(){
        return new Info()
                .title("DID 블록체인 API")
                .description("각 기관에 검증, 발급, 폐기를 관리하는 API")
                .version("1.0");
    }
}
