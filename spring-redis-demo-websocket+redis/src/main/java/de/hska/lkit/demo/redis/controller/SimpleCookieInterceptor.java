package de.hska.lkit.demo.redis.controller;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.tomcat.jni.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.ObjectUtils;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import de.hska.lkit.demo.redis.configuration.RedisConfiguration;

/**
 * 
 * CookieInterceptor: Lesen von Session Tokens aus Request Cookies
 *  Ein spezifischer HandlerInterceptorAdapter liest bei jedem Request die Cookies
 *  aus und durchsucht sie nach einem gültigen Session Token.
 *  Existiert ein gültiges Token, dann wird damit die Session initialisiert.
 */

public class SimpleCookieInterceptor extends HandlerInterceptorAdapter {

	@Override
	public boolean preHandle(HttpServletRequest req, HttpServletResponse res, Object handler) throws Exception {
		System.out.println("Klasse: SimpleCookieInterceptor, Methode: preHandle wurde aufgerufen.");
		
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
		   context.register(RedisConfiguration.class);
		  context.refresh();
		   StringRedisTemplate stringRedisTemplate = (StringRedisTemplate) context.getBean("stringRedisTemplate");
	
		   
		Cookie[] cookies = req.getCookies();
		if (!ObjectUtils.isEmpty(cookies))
			for (Cookie cookie : cookies) 
				if (cookie.getName().equals("auth")) {
					String auth = cookie.getValue();
					
					if (auth != null) {
						
						String username = stringRedisTemplate.opsForValue().get("auth:" + auth);
						//System.out.println("username aus redis auth:...: "+ username);
						
						System.out.println(username);
						if (username != null) {
							String name = (String) stringRedisTemplate.boundHashOps("user:" + username).get("username");
							System.out.println(name);
							SimpleSecurity.setUser(name, username);
							System.out.println("SimpleCookieInterceptor: SimpleSecurityname has been set as : " +name);
						}
						else{
							SimpleSecurity.setUser(null, null); 
							System.out.println("Inside SimpleCookie Interceptor and setted SimpleSec-User to null"+SimpleSecurity.isSignedIn());
						}
						}//if
				}//if
	
		return true;
	} // clean up SimpleSession State in the end (skipped here) 
}