package de.hska.lkit.demo.redis.controller;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
	@Autowired
	private StringRedisTemplate template;

	@Override
	public boolean preHandle(HttpServletRequest req, HttpServletResponse res, Object handler) throws Exception {
		System.out.println("Klasse: SimpleCookieInterceptor, Methode: preHandle wurde aufgerufen.");
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
		   context.register(RedisConfiguration.class);
		  context.refresh();
		   StringRedisTemplate stringRedisTemplate = (StringRedisTemplate) context.getBean("stringRedisTemplate");
		   
		   try {
			   Cookie[] cookies = req.getCookies();
			   //anfang eigene syso
			   Cookie cookieeeee;
				System.out.println(cookies.length);
				for(int i=0; i<cookies.length;i++) {
					cookieeeee= cookies[i];
				System.out.println("drucke inhalt cookie: "+cookies[i]);
				System.out.println("Name : " + cookieeeee.getName( ) + ",  ");
				System.out.println("Value: " + cookieeeee.getValue( )+" <br/>");				
				}//ende eigene syso
				
				if (!ObjectUtils.isEmpty(cookies)) {
					System.out.println("Abfrage ObjectUtils leer? :" + !ObjectUtils.isEmpty(cookies));
					for (Cookie cookie : cookies) {
						if (cookie.getName().equals("auth")) {
							System.out.println("heiß die cokkie auth? :" + cookie.getName().equals("auth"));
							String auth = cookie.getValue();
							System.out.println("gibt mir value von cookie und setz es in auth: "+ auth);
							if (auth != null) {
								System.out.println("ist auth != null: " + (auth != null));
								String uname = stringRedisTemplate.opsForValue().get("auth:" + auth + ":uid");
								System.out.println("Redis-server abfrage nach inhalt von key: " + stringRedisTemplate.opsForValue().get("auth:" + auth + ":uid"));
								System.out.println("Das UID/eigt. username , genommen aus redis vom key 'auth:'+auth+':uid'" + uname);
								System.out.println(uname != null);
								if (uname != null) {
									String name = (String) stringRedisTemplate.boundHashOps("user:" + uname ).get("username");
									System.out.println("name des current logged in user: " + name);
									SimpleSecurity.setUser(name, uname);
									System.out.println("Name und UID in SimpleSecurity " + SimpleSecurity.getName()+" and "+ SimpleSecurity.getUid());
								}//if
							}//if
						}//if
					}
				}
			
		} catch (NullPointerException e) {
			// TODO: handle exception
			System.out.println("nullpointer");
		}
		
	
		return true;
	} // clean up SimpleSession State in the end (skipped here) 
}