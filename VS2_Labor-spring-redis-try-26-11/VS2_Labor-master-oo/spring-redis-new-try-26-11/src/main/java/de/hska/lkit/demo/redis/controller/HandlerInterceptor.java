package de.hska.lkit.demo.redis.controller;

import javax.servlet.http.*;
import org.springframework.web.servlet.ModelAndView;
//weeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee
public interface HandlerInterceptor {
	boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler);

	void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView);

	void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex);
}
