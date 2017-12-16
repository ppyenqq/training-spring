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
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import de.hska.lkit.demo.redis.model.Session;
import de.hska.lkit.demo.redis.model.User;
import de.hska.lkit.demo.redis.repo.SessionRepository;
import de.hska.lkit.demo.redis.repo.UserRepository;

/**
 * Controller for the log in, log out and registering a new user.
 * 
 * Author : Patricia Djami
 */
@Controller
public class LoginController {

	@Autowired
	private StringRedisTemplate stringRedisTemplate;
	
	@Autowired
	private UserRepository userRepo;

	@Autowired
	private RedisRepository repository;

	private Cookie[] allCookies;

	private static final Duration TIMEOUT = Duration.ofMinutes(5);

	/**
	 * Redirects to the login page. If a user is still signed in, it will redirect to the home.html.
	 * 
	 * @param user user
	 * @param model model
	 * @return login.html
	 */
	@RequestMapping(value = {"login"}, method = RequestMethod.GET)
	public String showLoginPage(@ModelAttribute User user, Model model) {
		return "login";
	}

	
	
	/**
	 * Registering a new user, means adding a new user. If such user already exist in the repository,
	 * it will redirect back to login page.
	 * 
	 * @param user user
	 * @param model model
	 * @return redirect to logsuc.html if registration is successful, otherwise redirect back to
	 *         login.html.
	 */
	@RequestMapping(value = {"login"}, method = RequestMethod.POST, params = "register")
	public String register(@ModelAttribute User user, HttpServletResponse response, Model model) {
		if (isUserRegistered(user)) {
			System.out.println(
					"There's already a user with this username, please sign in or use different username.");
			return "redirect:login";
		} else {
			userRepo.saveUser(user);
			model.addAttribute("message", "User successfully added");
			Map<String, User> retrievedUsers = userRepo.getAllUsers();
			model.addAttribute("users", retrievedUsers);
			String auth = repository.addAuth(user.getUsername(), TIMEOUT.getSeconds(),
					TimeUnit.SECONDS);
			Cookie cookie = new Cookie("auth", auth);
			response.addCookie(cookie);
//			Cookie cookie = new Cookie("user",user.getUsername());
//			response.addCookie(cookie);

			return "redirect:/logsuc/"+user.getUsername();
		}
	}
	/**
	 * Logging in for a registered user. Checks the user's password and start a session after a
	 * successful logging. If the user is not registered, redirects to login.html.
	 * 
	 * @param user user
	 * @param model model
	 * @return redirect to home.html if sign in is successful, otherwise redirect back to login.html.
	 */
	@RequestMapping(value = {"login"}, method = RequestMethod.POST, params = "signin")
	public String login(@ModelAttribute("user") @Valid User user, HttpServletResponse response, Model model) {
		System.out.println("trying to log in.");
		if (isUserRegistered(user)) {
			if (repository.auth(user.getUsername(), user.getPassword())) {
				String auth = repository.addAuth(user.getUsername(), TIMEOUT.getSeconds(),
						TimeUnit.SECONDS);
				Cookie cookie = new Cookie("auth", auth);
				response.addCookie(cookie);
//				Session session = new Session();
//				sessionRepo.saveSession(session, cookie.getValue());
				return "redirect:/home";
			}
		}
		return "redirect:login";
	}

	// für logsuc mapping get
		@RequestMapping(value = "/logsuc/{username}", method = RequestMethod.GET)
		public String logsuc(@PathVariable(value="username") String username,@ModelAttribute User user, Model model) {
			System.out.println("***Klasse:UserController, Methode: logsucGET wurde aufgerufen.***");
			
			return "logsuc";
		}
		// für logsuc mapping post 
		@RequestMapping(value = "/logsuc/{username}", method = RequestMethod.POST)
		public String logsuc2(@PathVariable(value="username") String username,@ModelAttribute User user, Model model) {
			System.out.println("***Klasse:UserController, Methode: logsucPOST wurde aufgerufen.***");
			System.out.println("user.getname in lodsuc: "+user.getUsername());
			System.out.println(username);
			model.addAttribute("koi", username);
			System.out.println("hello2");
			return "redirect:/home";
		}
	
	/**
	 * Log a user out. Will redirect user to login.html.
	 * 
	 * @param user user
	 * @param model model
	 * @return
	 */
	@RequestMapping(value = {"/logout"}, method = RequestMethod.GET)
	public String logout(@ModelAttribute User user, Model model, HttpServletRequest request) {
		Cookie[] cookies = request.getCookies();
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals("auth")) {
					String auth = cookie.getValue();
					System.out.println("gibt auth von cookie"+auth);
					String uname = stringRedisTemplate.opsForValue().get("auth:" + auth );
					repository.deleteAuth(uname);
				}
			}
//		if (SimpleSecurity.isSignedIn()) {
//			String name = SimpleSecurity.getName();
//			repository.deleteAuth(name);
//		}
			
		return "redirect:/login";
	}

	/**
	 * Check if the given user is in the database.
	 * 
	 * @param user user
	 * @return true if user is registered, false if user cannot be found in the database.
	 */
	private boolean isUserRegistered(User user) {
		Map<String, User> allUsers = userRepo.getAllUsers();
		String userKey = "user:" + user.getUsername();
		return allUsers.containsKey(userKey);
	}

	public Cookie[] getAllCookies() {
		return allCookies;
	}

	public void setAllCookies(Cookie[] allCookies) {
		this.allCookies = allCookies;
	}
}
