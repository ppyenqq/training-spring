package de.hska.lkit.demo.redis.controller;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.ObjectUtils;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

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
		Cookie[] cookies = req.getCookies();
		if (!ObjectUtils.isEmpty(cookies))
			for (Cookie cookie : cookies)
				if (cookie.getName().equals("auth")) {
					String auth = cookie.getValue();
					if (auth != null) {
						String uid = template.opsForValue().get("auth:" + auth + ":uid");
						if (uid != null) {
							String name = (String) template.boundHashOps("uid:" + uid + ":user").get("name");
							SimpleSecurity.setUser(name, uid);
						}//if
					}//if
				}//if
	
		return true;
	} // clean up SimpleSession State in the end (skipped here) 
}