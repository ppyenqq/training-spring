package de.hska.lkit.demo.redis.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import de.hska.lkit.demo.redis.model.User;
import de.hska.lkit.demo.redis.repo.UserRepository;

/**
 * @author knad0001
 *
 */
@Controller
public class UserController {
	
	//ich brauch den userrepository um auf Daten im Redis DB zuzugreifen
	private final UserRepository userRepository;

	@Autowired
	public UserController(UserRepository userRepository) {
		super();
		this.userRepository = userRepository;
	}

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
	@RequestMapping(value = "/users", method = RequestMethod.GET)
	public String getAllUsers(Model model) {
		//hiermmit hol ich mir alle User aus der Repository raus (bemekung: die sind nicht geordnet)
		Map<String, User> retrievedUsers = userRepository.getAllUsers();
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
		User found = userRepository.getUser(username);

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
		return "newUser";
	}

	/**
	 * add a new user, adds a list of all users to model
	 * hier wwurde method POST verwendet da Daten abgeschickt werden
	 * @param user
	 *            User object filled in form
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/adduser", method = RequestMethod.POST)
	//zu @ModelAttribute: es sagt ja im newUser.html gibs nen Formular, alle Daten die dort eingefügt 
	//werden werden in diesen user gespeichert , in der methode  wird der dann in den repository gespeichert wird
	public String saveUser(@ModelAttribute User user, Model model) {

		userRepository.saveUser(user);
		model.addAttribute("message", "User successfully added");
	//nachdem der user gespeichert wurde bekommt man die Liste aller User zu sehen, die methoden unten ist wie getAllUser() oben
		Map<String, User> retrievedUsers = userRepository.getAllUsers();

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

		Map<String, User> retrievedUsers = userRepository.findUsersWith(pattern);

		model.addAttribute("users", retrievedUsers);
		return "users";
	}
	
	
	

}
