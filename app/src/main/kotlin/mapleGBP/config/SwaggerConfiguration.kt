package mapleGBP.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Controller
import springfox.documentation.builders.ApiInfoBuilder
import springfox.documentation.builders.PathSelectors
import springfox.documentation.builders.RequestHandlerSelectors
import springfox.documentation.service.ApiInfo
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spring.web.plugins.Docket
import springfox.documentation.swagger2.annotations.EnableSwagger2

@Configuration
@EnableSwagger2
open class SwaggerConfiguration {

    @Bean
    open fun docket(): Docket {
        val groupName: String = "V1"
        return Docket(DocumentationType.SWAGGER_2)
            .select()
            .apis(RequestHandlerSelectors.basePackage("mapleGBP.controller"))
            .paths(PathSelectors.any())
            .build()
            .groupName(groupName)
            .pathMapping("/")
            .apiInfo(apiInfo())
    }

    private fun apiInfo(): ApiInfo {
        return ApiInfoBuilder()
            .title("Boss Party Manage API")
            .description("메이플 보스 파티 관리용 서버 API")
            .version("v0.0.1")
            .build()
    }

}