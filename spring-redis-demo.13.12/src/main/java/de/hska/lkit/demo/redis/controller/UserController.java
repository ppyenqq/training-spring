package de.hska.lkit.demo.redis.controller;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import de.hska.lkit.demo.redis.model.Post;
import de.hska.lkit.demo.redis.model.User;
import de.hska.lkit.demo.redis.repo.PostRepository;
import de.hska.lkit.demo.redis.repo.SessionRepository;
import de.hska.lkit.demo.redis.repo.UserRepository;

/**
 * @author knad0001
 *
 */
@Controller
public class UserController {
	
	
	//ich brauch den userrepository um auf Daten im Redis DB zuzugreifen
	@Autowired
	private final UserRepository userRepo;
	
	@Autowired
	public LoginController loginController;
	
	@Autowired
	private PostRepository postRepository;
	
	//variable für private und global Post; private ist true ; global ist false
		private boolean postDisplay = true;
	
	@Autowired
	public UserController(UserRepository userRepo, SessionRepository sessionRepo) {
		super();
		this.userRepo = userRepo;
//		this.sessionRepo = sessionRepo;
	}
	
	/*********************************************************************************
	 *********************************************************************************
	 *
	 *  Simple Request Mappings for navigations. More functional calls can be added later.
	 *  
	 *********************************************************************************
	 *********************************************************************************
	 */
	
	/**
	 * redirect to user's homepage after login
	 * 
	 * @return
	 */
	@RequestMapping(value = "/home/{username}", method = RequestMethod.GET)
	public String gotoHome(@ModelAttribute User user, HttpServletRequest request, Model model, @ModelAttribute Post post, @RequestParam(value="startSearching", required=false) String searchPatternn,  @RequestParam(value="spattern" ,required = false) String searchPattern,@ModelAttribute("spattern") String searchPattern3) {
		System.out.println("***Klasse:UserController, Methode: gotoHome wurde aufgerufen.***");
		// TODO get username from Interceptor.
		String uid = SimpleSecurity.getUid();
		String name = SimpleSecurity.getName();
		user.setUsername(SimpleSecurity.getName());
		
//		Cookie[] cookies = request.getCookies();
//		for (Cookie cookie:cookies){
//			if (cookie.getName().contentEquals("user")){
//				user.setUsername(cookie.getValue());
//			}
//		}
		
		if(postDisplay==true) { 
			try {
				List<Post> oneUsersPost = postRepository.getOneUsersPost(user.getUsername());
				 model.addAttribute("oneUserPostlist",  oneUsersPost);
				 
				 for(Post lisofUserPast : oneUsersPost) {
			            System.out.println(lisofUserPast.getContent());
			        }
			} catch (NullPointerException e) {
				// TODO: handle exception
				System.out.println("nullpointer: steht noch nix im Post");
			}
	    } else {
	    	try {
				List<Post> allUsersPost = postRepository.getAllUsersPost();
				 model.addAttribute("oneUserPostlist",  allUsersPost);
				 
				 for(Post lisofUserPast : allUsersPost) {
			            System.out.println(lisofUserPast.getContent());
			        }
	    	} catch (NullPointerException e) {
			// TODO: handle exception
			System.out.println("nullpointer: steht noch nix im Post");
	    	}
	    }
		//part for search
		
				if(searchPattern3==null || searchPattern3.isEmpty()) {
					System.out.println("searchpattern: leer oder null ");
				Set <String> searchAllUserDisplay = userRepo.displayUserZSET();
				 model.addAttribute("allSearchDisplay",  searchAllUserDisplay);
				
				}else {			
					System.out.println("searchPattern: " + searchPattern3);
					model.addAttribute("allSearchDisplay",  userRepo.findUsersWith(searchPattern3));
					model.addAttribute("searchFlag", true);
				}
		return "home";
	}
	
	/**
	 * Für Post-Button
	 * @param username
	 * @param user
	 * @param model
	 * @param post
	 * @return
	 */
	@RequestMapping(value = "/home/{username}", method = RequestMethod.POST)
	public String homePost(@PathVariable(value="username") String username,@ModelAttribute User user, Model model, @ModelAttribute Post post) {
		System.out.println("***Klasse:UserController, Methode: signIn wurde aufgerufen.***");
		post.setAuthorId(username);
		System.out.println(post.getAuthorId());
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date date = new Date();
		System.out.println("datum und uhrzeit: " + dateFormat.format(date));
		post.setDate(dateFormat.format(date));
		postRepository.savePost(post);
		 
		return "redirect:/home/"+username;
	}
	
	@RequestMapping(value = "/home/{username}", method = RequestMethod.POST, params="global")
	public String globalButtonPost(@PathVariable(value="username") String username,@ModelAttribute User user, Model model, @ModelAttribute Post post) {
		System.out.println("***Klasse:UserController, Methode: signIn wurde aufgerufen.***");
		System.out.println("global runningggggggggggggggggggggggggggggggggggg");
		postDisplay = false;
		return "redirect:/home/"+ username;
	}
	@RequestMapping(value = "/home/{username}", method = RequestMethod.POST, params="private")
	public String privateButtonPost(@PathVariable(value="username") String username,@ModelAttribute User user, Model model, @ModelAttribute Post post) {
		System.out.println("***Klasse:UserController, Methode: signIn wurde aufgerufen.***");
		System.out.println("private runningggggggggggggggggggggggggggggggggggg");
		postDisplay = true;
		return "redirect:/home/"+ username;
	}
	@RequestMapping(value = "/home/{username}", method = RequestMethod.POST, params="startSearching")
	public String searchUser2(RedirectAttributes redirectAttribut, @PathVariable(value="username") String username,@ModelAttribute User user, Model model, @ModelAttribute Post post, @RequestParam("startSearching") String pattern) {
		System.out.println("***Klasse:UserController, Methode: search wurde aufgerufen.***");		
		System.out.println(pattern);
		redirectAttribut.addFlashAttribute("spattern", pattern);
		redirectAttribut.toString();


		// model.addAttribute("allSearchDisplay",  userRepo.findUsersWith(pattern));
		return "redirect:/home/"+ username;
	}
	
	
	/**
	 * redirect to other user's homepage
	 * 
	 * @return
	 */
	@RequestMapping(value = "/other", method = RequestMethod.GET)
	public String getOtherUsersHomepage(@ModelAttribute User user) {
		System.out.println("***Klasse:UserController, Methode: getOtherUsersHomepage wurde aufgerufen.***");
		return "othershome";
	}
	
	/**
	 * redirect to user's following page
	 * 
	 * @return
	 */
	@RequestMapping(value = "/user/following", method = RequestMethod.GET)
	public String getUsersFollowing(@ModelAttribute User user, Model model) {
		System.out.println("***Klasse:UserController, Methode: getUsersFollowing wurde aufgerufen.***");
	
		//hiermmit hol ich mir alle User aus der Repository raus (bemekung: die sind nicht geordnet)
		Map<String, User> retrievedUsers = userRepo.getAllFollowers(user);
		
		//fügt dem model die ganzen Users zu und beeinflusst gleichzeitig die View/HTML, wenn ihr in
		// "users.html" guckt gibt es ne deklaration "elemenent : ${users}", das sagt dem HTML ja an diesem platz
		// kannst du den User und seine Daten hinzufügen
		model.addAttribute("follower", retrievedUsers);
		
		// gibt mir die HTML-Seite users zurück 
		return "following";
	}
	
	/**
	 * redirect to user's following page
	 * 
	 * @return
	 */
	@RequestMapping(value = "/user/{username}", method = RequestMethod.POST, params="global")
	public String addFollower(@PathVariable("username") String username, @ModelAttribute User user,Model model) {
		System.out.println("***Klasse:UserController, Methode: addFollower wurde aufgerufen.***");
		
		// in Redis wird der Follower hinzugefuegt
		userRepo.saveFollower(user, username);
		model.addAttribute("message", "Follower successfully added");
		return "following";
	}
	
	
	
	/*********************************************************************************
	 *********************************************************************************
	 *
	 * Below are the Request Mappings from Zirpins.
	 * 
	 *********************************************************************************
	 *********************************************************************************
	 */

	/**
	 * list all users
	 * value="/users" sagt aus dass wenn ich im Browser  localhost:8080/users anfordere, 
	 * dann mach das und das und gibt mir anschließend die HTML mit den hinzugefügten/geänderten Daten zurück
	 * 
	 * es gibt bei den Methoden 2 arten GET und POST (ihr könnt die auch weglassen und es funktioniert trozdem, glaub ich)
	 * grob gesagt: GET ist enhüllter / unsicherer als POST , wenn ihr mit GET Daten/Formular an den Server abschick, wird der
	 * alle Daten im URL auf aufzeigen www.ndj.com/firstname/bob/lastname/kiki/..., währenddessen wenn ihr mit POST
	 * Daten abschick wird das sich nicht im URL wiederspiegeln
	 * 
	 * @param model
	 * 
	 * @return
	 */
//	@RequestMapping(value = "/users", method = RequestMethod.GET)
	public String getAllUsers(Model model) {
		System.out.println("***Klasse:UserController, Methode: getAllUsers(Model) wurde aufgerufen.***");
		//hiermmit hol ich mir alle User aus der Repository raus (bemekung: die sind nicht geordnet)
		Map<String, User> retrievedUsers = userRepo.getAllUsers();
		//fügt dem model die ganzen Users zu und beeinflusst gleichzeitig die View/HTML, wenn ihr in
		// "users.html" guckt gibt es ne deklaration "elemenent : ${users}", das sagt dem HTML ja an diesem platz
		// kannst du den User und seine Daten hinzufügen
		model.addAttribute("users", retrievedUsers);
		// gibt mir die HTML-Seite users zurück 
		return "users";
	}

	/**
	 * get information for user with username
	 * 
	 * mit @PathVariable("username") sagt man einfach ich möchte den usernamen im URL benutzen, also
	 * wie ihr bei value = "/user/{username}" sehen könnt, wenn ich jetzt aufrufe localhost:8080/user/anna
	 * dann zeigt es mit Daten von anna an
	 * 
	 * @param username
	 *            username to find
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/user/{username}", method = RequestMethod.GET)
	public String getOneUsers(@PathVariable("username") String username, Model model) {
		System.out.println("***Klasse:UserController, Methode: getOneUsers() wurde aufgerufen.***");
		User found = userRepo.getUser(username);

		model.addAttribute("userFound", found);
		return "home";
	}
	
	/**
	 * redirect to page to add a new user
	 * 
	 * @return
	 */
	@RequestMapping(value = "/adduser", method = RequestMethod.GET)
	public String addUser(@ModelAttribute User user) {
		System.out.println("***Klasse:UserController, Methode: addUser wurde aufgerufen.***");
		return "adduser";
}
	
	/**
	 * add a new user, adds a list of all users to model
	 * hier wurde method POST verwendet da Daten abgeschickt werden
	 * @param user
	 *            User object filled in form
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/adduser", method = RequestMethod.POST)
	//zu @ModelAttribute: es sagt ja im newUser.html gibs nen Formular, alle Daten die dort eingefügt 
	//werden werden in diesen user gespeichert , in der methode  wird der dann in den repository gespeichert wird
	public String saveUser(@ModelAttribute User user, Model model) {
		System.out.println("***Klasse:UserController, Methode: saveUser wurde aufgerufen.***");

		userRepo.saveUser(user);
		model.addAttribute("message", "User successfully added");
	//nachdem der user gespeichert wurde bekommt man die Liste aller User zu sehen, die methoden unten ist wie getAllUser() oben
		Map<String, User> retrievedUsers = userRepo.getAllUsers();

		model.addAttribute("users", retrievedUsers);
		return "users";
	}
	
	
	/**
	 * search usernames containing the sequence of characters
	 * 
	 * @param user
	 *            User object filled in form
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/searchuser/{pattern}", method = RequestMethod.GET)
	public String searchUser(@PathVariable("pattern") String pattern, @ModelAttribute User user, Model model) {
		System.out.println("***Klasse:UserController, Methode: searchUser wurde aufgerufen.***");

		Set<String> retrievedUsers = userRepo.findUsersWith(pattern);

		model.addAttribute("users", retrievedUsers);
		return "users";
	}
	
	
	

}
