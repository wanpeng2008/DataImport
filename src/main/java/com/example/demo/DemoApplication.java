package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;

import javax.servlet.MultipartConfigElement;
import java.io.File;

@SpringBootApplication
public class DemoApplication {
/*	@Bean
	MultipartConfigElement multipartConfigElement() {
		MultipartConfigFactory factory = new MultipartConfigFactory();
		factory.setLocation("ERPUpload");
		File uploadFolder = new File("RPUpload");
		if(!uploadFolder.exists()){
			uploadFolder.mkdir();
		}
		return factory.createMultipartConfig();
	}*/
	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}
}


