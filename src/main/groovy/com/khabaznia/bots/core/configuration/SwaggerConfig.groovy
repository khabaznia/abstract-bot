package com.khabaznia.bots.core.configuration

import com.fasterxml.classmate.TypeResolver
import com.khabaznia.bots.core.integration.dto.ErrorResponseDto
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import springfox.documentation.builders.PathSelectors
import springfox.documentation.builders.RequestHandlerSelectors
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spring.web.plugins.Docket

@Configuration
class SwaggerConfig {

    @Value('${env.only.bot.token}')
    private String botToken

    @Bean
    Docket appApi(TypeResolver typeResolver) {
        new Docket(DocumentationType.SWAGGER_2)
                .ignoredParameterTypes(MetaClass.class)
                .additionalModels(typeResolver.resolve(ErrorResponseDto.class))
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.any()
                        .and(PathSelectors.regex("/error").negate())
                        .and(PathSelectors.regex("/$botToken").negate()))
                .build()
    }
}
