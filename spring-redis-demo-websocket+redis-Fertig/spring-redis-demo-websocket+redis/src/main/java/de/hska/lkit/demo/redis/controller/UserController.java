package de.hska.lkit.demo.redis.controller;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Set;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import de.hska.lkit.demo.redis.model.Post;
import de.hska.lkit.demo.redis.model.SimpleSecurity;
import de.hska.lkit.demo.redis.model.User;
import de.hska.lkit.demo.redis.repo.PostRepository;
//import de.hska.lkit.demo.redis.repo.SessionRepository;
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
	
	//@Autowired
	//private RedisTemplate<String, Object> reTemplate;
	
	
	//variable für private und global Post; private ist true ; global ist false
		private boolean postDisplay = true;
	@Autowired	
	private SimpMessagingTemplate simpleMessTemplate;
	@Autowired
	private RedisTemplate<String, Post> reTemplate;
	
	@Autowired
	public UserController(UserRepository userRepo) {
		super();
		this.userRepo = userRepo;
	}
	
	/*********************************************************************************
	 *********************************************************************************
	 *
	 *  Simple Request Mappings for navigations. More functional calls can be added later.
	 *  
	 *********************************************************************************
	 *********************************************************************************
	 */

	
	@MessageMapping("/chat-room")
	@RequestMapping(path="/post/new", method=RequestMethod.POST)
	public void newPostBySocket(Post post)throws Exception {
		System.out.println("I am inside newPostby Socket. ");
		//Speichere den Post in Redis
		Post postForRedis = new Post();
		postForRedis.setAuthorId(post.getAuthorId());
		System.out.println("Inside newPostbySocket and starting too save in Redis and the author is: "+post.getAuthorId());
		DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
		Date date = new Date();
		System.out.println("datum und uhrzeit: " + dateFormat.format(date));
		postForRedis.setDate(dateFormat.format(date));
		postForRedis.setContent(post.getContent());
		System.out.println("Inseide newPostBy Socket and the content is: "+ post.getContent());
		postRepository.savePost(postForRedis);
		//end des speichern des Posts in Redis
		
		this.reTemplate.convertAndSend("/topic/chat-room", post);		
	   // Thread.sleep(500);//der redis container braucht bissle zeit bis er die nachricht von redischannel abfängt, wenn ich das nicht mache  wird simpleTemplate.convertandsend sofort ausgeführt aber in den subscriber wurde noch nix gespeichert	
	
	}
	/**
	 * redirect to user's homepage after login
	 * 
	 * @return
	 */
	@RequestMapping(value = "/home", method = RequestMethod.GET)
	public String gotoHome(@ModelAttribute User user, HttpServletRequest request, Model model, @ModelAttribute Post post,@RequestParam(value="startSearching", required=false) String searchPatternn,  @RequestParam(value="spattern" ,required = false) String searchPattern,@ModelAttribute("spattern") String searchPattern3) {
		System.out.println("***Klasse:UserController, Methode: gotoHome wurde aufgerufen.***");
		// TODO get username from SimpleInterceptor.
		if (SimpleSecurity.isSignedIn()) {		
		user.setUsername(SimpleSecurity.getName());
		
		//teil für redis post channel subscribe, hol liste von leute ch follow und schick an th:inline javascript
		Set<String> userIFollow= userRepo.getPeopleIFollow(user.getUsername());
		model.addAttribute("userIFollowSet", userIFollow);
				
		//teil für post privat und global
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
			
		Set <String> searchAllUserDisplay = userRepo.displayUserZSET();
		 model.addAttribute("allSearchDisplay",  searchAllUserDisplay);
		
		}else {			
			
			model.addAttribute("allSearchDisplay",  userRepo.findUsersWith(searchPattern3));
			model.addAttribute("searchFlag", true);
		}
			return "home";
	
		}else {
			return "redirect:/login";
		}
	}
	
	/**
	 * Für Post-Button
	 * @param username
	 * @param user
	 * @param model
	 * @param post
	 * bei Aufgabe 2 wurde diese benutzt um Post in Redis zu speichern,
	 * da bei aufgabe 3 Post dynamisch, also ohne erneutes laden funktionieren soll, musste dese auskommentiert werden
	 * und in die newPostByWebsocket rüberkopiert werden
	 * @return
	 */
//Bevor Aufgabe 3 war dies die Methode , die die Post abspeichert, jezt wurde sie nach oben verlegt
//	@RequestMapping(value = "/home", method = RequestMethod.POST)
//	public void homePost(@ModelAttribute User user, Model model, @ModelAttribute Post post) {
//		
//		System.out.println("***Klasse:UserController, Methode: signIn wurde aufgerufen.***");
//		post.setAuthorId(SimpleSecurity.getName());
//		System.out.println(post.getAuthorId());
//		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
//		Date date = new Date();
//		System.out.println("datum und uhrzeit: " + dateFormat.format(date));
//		post.setDate(dateFormat.format(date));
//		postRepository.savePost(post);
//		 
//		//return "";
//	}
	
	@RequestMapping(value = "/home", method = RequestMethod.POST, params="global")
	public String globalButtonPost(@ModelAttribute User user, Model model, @ModelAttribute Post post) {
		System.out.println("***Klasse:UserController, Methode: signIn wurde aufgerufen.***");
		System.out.println("global runningggggggggggggggggggggggggggggggggggg");
		postDisplay = false;
		return "redirect:/home";
	}
	@RequestMapping(value = "/home", method = RequestMethod.POST, params="private")
	public String privateButtonPost(@ModelAttribute User user, Model model, @ModelAttribute Post post) {
		System.out.println("***Klasse:UserController, Methode: signIn wurde aufgerufen.***");
		System.out.println("private runningggggggggggggggggggggggggggggggggggg");
		postDisplay = true;
		return "redirect:/home";
	}
	@RequestMapping(value = "/home", method = RequestMethod.POST, params="startSearching")
	public String searchUser2(RedirectAttributes redirectAttribut,@ModelAttribute User user, Model model, @ModelAttribute Post post, @RequestParam("startSearching") String pattern) {
		System.out.println("***Klasse:UserController, Methode: search wurde aufgerufen.***");		
		System.out.println(pattern);
		redirectAttribut.addFlashAttribute("spattern", pattern);
		return "redirect:/home";
	}
	
	/**
	 * redirect to user's following page
	 * 
	 * @return
	 */
	@RequestMapping(value = "/following", method = RequestMethod.GET)
	public String getUsersFollowing(@ModelAttribute User user, Model model) {
		System.out.println("***Klasse:UserController, Methode: getUsersFollowing wurde aufgerufen.***");
	
		//hiermmit hol ich mir alle User aus der Repository raus (bemekung: die sind nicht geordnet)
		//falls user.getUsername() nicht funktioniert Simplesecurity.getname() benutzen, "/following" muss aber vorher in Webconfig-Klasse bei Interceptor zu den addPathPatterns hinzugefügtwerden
		Set<String> retrievedUsers = userRepo.getPeopleIFollow(SimpleSecurity.getName());
		
		//fügt dem model die ganzen Users zu und beeinflusst gleichzeitig die View/HTML, wenn ihr in
		// "users.html" guckt gibt es ne deklaration "elemenent : ${users}", das sagt dem HTML ja an diesem platz
		// kannst du den User und seine Daten hinzufügen
		model.addAttribute("followingSET", retrievedUsers);
		return "following";
	}
	
	/**
	 * redirect to user's follower page
	 * 
	 * @return
	 */
	@RequestMapping(value = "/follower", method = RequestMethod.GET)
	public String getUsersFollower(@ModelAttribute User user, Model model) {
		System.out.println("***Klasse:UserController, Methode: getUsersFollowing wurde aufgerufen.***");
	
		Set<String> retrievedUsers = userRepo.getAllFollowers(SimpleSecurity.getName());
		model.addAttribute("follower", retrievedUsers);
		return "follower";
	}
	
	/**
	 * drücken der follow-Button aus Seite von anderen Usern
	 * 
	 * @return
	 */
	@RequestMapping(value = "/user/{username}", method = RequestMethod.POST, params="follow")
	public String addFollower(@PathVariable("username") String username, @ModelAttribute User user,Model model) {
		System.out.println("***Klasse:UserController, Methode: addFollower wurde aufgerufen.***");
		
		// in Redis wird der Follower hinzugefuegt
		System.out.println("login as" + SimpleSecurity.getName() + " and follows " + username);
		userRepo.saveFollower(SimpleSecurity.getName(), username);
		model.addAttribute("message", "Follower successfully added");
		return "redirect:/user/"+username;
	}
	
	/**
	 * drücken der unfollow-Button aus Seite von anderen Usern
	 * 
	 * @return
	 */
	@RequestMapping(value = "/user/{username}", method = RequestMethod.POST, params="unfollow")
	public String unfollower(@PathVariable("username") String username, @ModelAttribute User user,Model model) {
		System.out.println("***Klasse:UserController, Methode: addFollower wurde aufgerufen.***");
		
		// in Redis wird der Follower hinzugefuegt
		System.out.println("login as" + SimpleSecurity.getName() + " and follows " + username);
		userRepo.removeFollower(SimpleSecurity.getName(), username);
		model.addAttribute("message", "Follower successfully removed");
		return "redirect:/user/"+username;
	}
	
	
	/**
	 * show other user's homepage
	 * 
	 * @return
	 */
	@RequestMapping(value = "/user/{username}", method = RequestMethod.GET)
	public String getOtherUsersHomepage(@PathVariable("username") String username, @ModelAttribute User user, Model model,@RequestParam(value="startSearching", required=false) String searchPatternn,@ModelAttribute("spattern") String searchPattern3) {
		System.out.println("***Klasse:UserController, Methode: getOtherUsersHomepage wurde aufgerufen.***");
		model.addAttribute("otheruser", username);
		List<Post> oneUsersPost = postRepository.getOnlyOneUsersPost(username);
		 model.addAttribute("otherUserPostlist",  oneUsersPost);
		 for(Post lisofUserPast : oneUsersPost) {
	            System.out.println(lisofUserPast.getContent());
	        }
		 //for search
		 if(searchPattern3==null || searchPattern3.isEmpty()) {
				
				Set <String> searchAllUserDisplay = userRepo.displayUserZSET();
				 model.addAttribute("allSearchDisplay",  searchAllUserDisplay);
				
				}else {			
					
					model.addAttribute("allSearchDisplay",  userRepo.findUsersWith(searchPattern3));
					model.addAttribute("searchFlag", true);
				}
		 boolean alreadyFollowing= userRepo.isFollowingOtherUser(SimpleSecurity.getName(),username);
		 model.addAttribute("alreadyFollowing", alreadyFollowing);
				
		return "othershome";
	}
	
	/**
	 * search in otherUsers page
	 */
	@RequestMapping(value = "/user/{username}", method = RequestMethod.POST, params="startSearching")
	public String searchInOthersHome(@PathVariable("username") String username, @ModelAttribute User user,Model model) {
		System.out.println("***Klasse:UserController, Methode: searchInOthersHome wurde aufgerufen.***");		
		return "redirect:/user/"+username;
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
/*	public String getAllUsers(Model model) {
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
	
	*//**
	 * redirect to page to add a new user
	 * 
	 * @return
	 *//*
	@RequestMapping(value = "/adduser", method = RequestMethod.GET)
	public String addUser(@ModelAttribute User user) {
		System.out.println("***Klasse:UserController, Methode: addUser wurde aufgerufen.***");
		return "adduser";
}
	
	*//**
	 * add a new user, adds a list of all users to model
	 * hier wurde method POST verwendet da Daten abgeschickt werden
	 * @param user
	 *            User object filled in form
	 * @param model
	 * @return
	 *//*
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
	
	
	*//**
	 * search usernames containing the sequence of characters
	 * 
	 * @param user
	 *            User object filled in form
	 * @param model
	 * @return
	 *//*
	@RequestMapping(value = "/searchuser/{pattern}", method = RequestMethod.GET)
	public String searchUser(@PathVariable("pattern") String pattern, @ModelAttribute User user, Model model) {
		System.out.println("***Klasse:UserController, Methode: searchUser wurde aufgerufen.***");

		Set<String> retrievedUsers = userRepo.findUsersWith(pattern);

		model.addAttribute("users", retrievedUsers);
		return "users";
	}*/
	
	
	

}
