package de.hska.lkit.demo.redis.controller;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;

@Configuration
@EnableWebMvc
@ComponentScan(basePackages = { "de.hska.lkit.demo.redis" })

/**
 * 
 * Interzeptoren in Spring Application Context Xml File konfigurieren
   oder
   In einer Java Klasse, die die Klasse WebMvcConfigurerAdapter erweitert,
   WebMvcConfigurerAdapter enthält die addInterceptors Methode, die als Zugriff
   zur InterceptorRegistry dient.
 *
 */
public class WebConfig extends WebMvcConfigurerAdapter {
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		//registry.addInterceptor(new LocaleChangeInterceptor());
		//registry.addInterceptor(new SimpleInterceptor()).addPathPatterns("/auth/**");
		registry.addInterceptor(new SimpleCookieInterceptor()).addPathPatterns("/login","/home","/logout", "/home/*");
	}
	
	
	//Da @EnableWebMVC meine Standard zugriff auf resourcen wie CSS und Images  deaktiviert, kopiere ich einfach den Standardzugriff Code und fügt/überschreibe es hier ein 
	private static final String[] CLASSPATH_RESOURCE_LOCATIONS = {
			"classpath:/META-INF/resources/", "classpath:/resources/",
			"classpath:/static/", "classpath:/public/" };
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		if (!registry.hasMappingForPattern("/webjars/**")) {
			registry.addResourceHandler("/webjars/**").addResourceLocations(
					"classpath:/META-INF/resources/webjars/");
		}
		if (!registry.hasMappingForPattern("/**")) {
			registry.addResourceHandler("/**").addResourceLocations(
					CLASSPATH_RESOURCE_LOCATIONS);
		}
	}
}
