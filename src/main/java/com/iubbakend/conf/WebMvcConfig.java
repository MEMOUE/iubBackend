package com.iubbakend.conf;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

	@Value("${app.upload.actualites.dir}")
	private String actualiteUploadDir;

	@Value("${app.upload.directeurs.dir}")
	private String directeurUploadDir;

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		// Configuration pour servir les images d'actualités
		registry.addResourceHandler("/uploads/actualites/**")
				.addResourceLocations("file:" + Paths.get(actualiteUploadDir).toAbsolutePath() + "/");

		// Configuration pour servir les images de directeurs
		registry.addResourceHandler("/uploads/directeurs/**")
				.addResourceLocations("file:" + Paths.get(directeurUploadDir).toAbsolutePath() + "/");
	}
}