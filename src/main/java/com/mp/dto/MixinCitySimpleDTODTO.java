package com.mp.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.boot.jackson.JsonMixin;

/**
 * Spring Boot 2.7 的Jackson自动配置将扫描应用程序的包以查找带有@JsonMixin注释的类，
 * 并将它们注册到自动配置的ObjectMapper。 注册动作由Spring Boot的JsonMixinModule执行。
 */
@JsonMixin(CitySimpleDTO.class)
public class MixinCitySimpleDTODTO {
    /**
     * 转json时将原有name的名字改成full_name
     */
    @JsonProperty("full_name")
    private String name;

}
