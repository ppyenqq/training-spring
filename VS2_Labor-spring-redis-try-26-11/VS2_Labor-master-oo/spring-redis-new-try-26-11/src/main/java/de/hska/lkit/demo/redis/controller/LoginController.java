package de.hska.lkit.demo.redis.controller;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import de.hska.lkit.demo.redis.model.Post;
import de.hska.lkit.demo.redis.model.User;
import de.hska.lkit.demo.redis.repo.PostRepository;

/**
 * LoginController: Controller-Klasse zum Aufsetzen neuer Sessions
 *  Der Controller verarbeitet ein Login Formular mit User Daten.
 *  Authentifizierung und Token-Management erfolgen im Repository.
 *  Bei erfolgreichem Login wird ein Cookie mit dem Token gesetzt.
 */
@Controller
public class LoginController {
	@Autowired
	private StringRedisTemplate stringRedisTemplate;
	@Autowired
	private PostRepository postRepository;
	
	@Autowired
	private RedisRepository repository;
	private static final Duration TIMEOUT = Duration.ofMinutes(15);
	
	@RequestMapping(value = {"/login"}, method = RequestMethod.GET)
	public String anmeldenShowPage(@ModelAttribute User user, Model model) {
		if (SimpleSecurity.isSignedIn()) {
			model.addAttribute("user", SimpleSecurity.getName());
			return "home";
		}
		
		return "login";
	}

	@RequestMapping(value = {"/login"}, method = RequestMethod.POST)
	public String login(@ModelAttribute("user") @Valid User user, HttpServletResponse response, Model model) {
	 // if (repository.auth(user.getName(), user.getPass())) {
		if (repository.auth(user.getUsername(), user.getPassword())) {
			
		//	String auth = repository.addAuth(user.getName(), TIMEOUT.get ...
			String auth = repository.addAuth(user.getUsername(), TIMEOUT.getSeconds(), TimeUnit.SECONDS);
			Cookie cookie = new Cookie("auth", auth);
			response.addCookie(cookie);
			
		 // model.addAttribute("user", user.getName());
			model.addAttribute("user", user.getUsername());
			
		//  return "users/" + user.getName();
		//	return "home/" + user.getUsername();
			return "redirect:/home";
		}
		model.addAttribute("user", new User());
		return "login";
	}
	/*@RequestMapping(value = "/login", method = RequestMethod.GET, params="forPost")
	public String addPost(@RequestParam("user") String username,@ModelAttribute Post post, Model model) {
		post.setAuthorId(username);
		postRepository.savePost(post);
		model.addAttribute("message", "User successfully added");

		Map<String, Post> retrievedPast = postRepository.getAllPost();

		model.addAttribute("pasts", retrievedPast);
		
		return "allPost";
	}*/
	
	@RequestMapping( value="/home")
	public String usersHOme(@RequestParam("user") String username, @ModelAttribute("user") @Valid User user, Model model) {
		
		model.addAttribute("user", username);
			return "home";
	
	}
	

	@RequestMapping(value = "/blog/logout", method = RequestMethod.GET)
	public String logout() {
		if (SimpleSecurity.isSignedIn()) {
			String name = SimpleSecurity.getName();
			repository.deleteAuth(name);
		}
		return "redirect:/";
	}
	@RequestMapping(value="/logout", method = RequestMethod.GET)
	public String logoutPage (HttpServletRequest req, HttpServletResponse res) {
		Cookie[] cookies = req.getCookies();
		   //anfang eigene syso
		   Cookie cookieeeee;
			System.out.println(cookies.length);
			for(int i=0; i<cookies.length;i++) {
				cookieeeee= cookies[i];
			System.out.println("drucke inhalt cookie22222222222222222222222: "+cookies[i]);
			System.out.println("Name222222222222 : " + cookieeeee.getName( ) + ",  ");
			System.out.println("Value:2222222222222 " + cookieeeee.getValue( )+" <br/>");				
			}//ende eigene syso
			
			for (Cookie cookie : cookies) {
				System.out.println("weeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee");
				if (cookie.getName().equals("auth")) {
					System.out.println("woooooooooooooooooooooooo");
					System.out.println("heiß die cokkie auth? :" + cookie.getName().equals("auth"));
					String auth = cookie.getValue();
					System.out.println("gibt mir value von cookie und setz es in auth: "+ auth);
					//String uname = SimpleSecurity.getName();
					String uname = stringRedisTemplate.opsForValue().get("auth:" + auth + ":uid");
					System.out.println("username syso from logout button: " + uname);
					repository.deleteAuth(uname);
				}
			}
		//System.out.println(SimpleSecurity.printHello());
		//System.out.println(SimpleSecurity.isSignedIn());
		//String name = SimpleSecurity.getName();
		//repository.deleteAuth(name);
	    return "redirect:/login";
	
	}
}
