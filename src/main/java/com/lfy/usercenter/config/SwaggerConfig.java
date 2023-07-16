package com.lfy.usercenter.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2WebMvc;

/**
 * 自定义 Swagger 接口文档的配置
 * http://localhost:8080/api/doc.html#/home
 * @Profile({"dev","test"}) 限定该接口文档在某些情况下不会暴露   dev--本机  prod--线上
 *
 * @author lfy
 */
@Configuration
@EnableSwagger2WebMvc
@Profile({"dev","test"})
public class SwaggerConfig {

    @Bean(value = "defaultApi2")
    public Docket defaultApi2() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                // 这里一定要标注你控制器的位置
                .apis(RequestHandlerSelectors.basePackage("com.lfy.usercenter.controller"))
                .paths(PathSelectors.any())
                .build();
    }

    /**
     * api 信息
     * @return
     */
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("玉米用户中心")
                .description("玉米用户中心接口文档")
                .termsOfServiceUrl("https://github.com/CornSixLove")
//                .contact(new Contact("lfy","https://github.com/CornSixLove","xxx@qq.com"))
                .version("1.0")
                .build();
    }
}
