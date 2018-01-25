package de.hska.lkit.demo.redis.configuration;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;

@Configuration
@ComponentScan(basePackages = { "org.my.package" })

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
		System.out.println("***Klasse:WebConfig, Methode: addInterceptors wurde aufgerufen.***");
		//registry.addInterceptor(new LocaleChangeInterceptor());
		//registry.addInterceptor(new SimpleInterceptor()).addPathPatterns("/auth/**");
		registry.addInterceptor(new SimpleCookieInterceptor()).addPathPatterns( "/logout","/home/**", "/user/**", "/follower", "/following", "/login");
	}
}
