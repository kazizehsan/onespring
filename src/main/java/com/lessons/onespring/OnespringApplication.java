package com.lessons.onespring;

import com.lessons.onespring.properties.FileStorageProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({
		FileStorageProperties.class
})
public class OnespringApplication {

	public static void main(String[] args) {
		SpringApplication.run(OnespringApplication.class, args);
	}

}
