package com.matdori.matdori;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@OpenAPIDefinition(servers = {@Server(url = "https://api.matdori.app", description = "서버 URL")})
@SpringBootApplication
public class MatdoriApplication {

	public static void main(String[] args) {
		SpringApplication.run(MatdoriApplication.class, args);
	}

}
