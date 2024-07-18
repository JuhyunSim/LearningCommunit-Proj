package com.zerobase.apigateway;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import reactivefeign.spring.config.EnableReactiveFeignClients;

@SpringBootApplication
@EnableDiscoveryClient
@ComponentScan(basePackages = {"com.zerobase.common", "com.zerobase.apigateway"})
@EnableEncryptableProperties
@PropertySource(name="EncryptedProperties", value = "classpath:application-common.yml")
@EnableReactiveFeignClients(basePackages = "com.zerobase.apigateway")
public class ApiGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class, args);
    }

}
