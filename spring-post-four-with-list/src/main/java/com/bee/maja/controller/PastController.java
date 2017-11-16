package com.bee.maja.controller;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.bee.maja.model.Past;
import com.bee.maja.repo.PastRepository;



@Controller
public class PastController {
	private final PastRepository pastRepository;

	@Autowired
	public PastController(PastRepository pastRepository) {
		super();
		this.pastRepository = pastRepository;
	}

	/**
	 * list all users
	 * 
	 * @param model
	 * 
	 * @return
	 */
	/*@RequestMapping(value = "/pasts", method = RequestMethod.GET)
	public String getAllPast(Model model) {
		Map<String, Past> retrievedPast = pastRepository.getAllPast();
		model.addAttribute("pasts", retrievedPast);

		return "pasts";
	}*/

	/**
	 * get information for user with username
	 * 
	 * @param username
	 *            username to find
	 * @param model
	 * @return
	 */
	/*@RequestMapping(value = "/user/{username}", method = RequestMethod.GET)
	public String getOneUsers(@PathVariable("username") String username, Model model) {
		User found = userRepository.getUser(username);

		model.addAttribute("userFound", found);
		return "oneUser";
	}*/

	/**
	 * redirect to page to add a new user
	 * 
	 * @return
	 */
	@RequestMapping(value = "/addpast", method = RequestMethod.GET)
	public String addPast(@ModelAttribute Past past) {
		return "newPast";
	}

	/**
	 * add a new user, adds a list of all users to model
	 * 
	 * @param user
	 *            User object filled in form
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/addpast", method = RequestMethod.POST)
	public String saveUser(@ModelAttribute Past past, Model model) {

		pastRepository.savePast(past);
		model.addAttribute("message", "User successfully added");

		Map<String, Past> retrievedPast = pastRepository.getAllPast();

		model.addAttribute("pasts", retrievedPast);
		List<String> nippi = new LinkedList();
		nippi.add("hello");
		model.addAttribute("heyo", nippi.get(0));
		//checke im redis-cli was für objekte in listPast und andere wie hash set sind
		List<String> listPast = pastRepository.getAllPastList();
		model.addAttribute("pastlist", listPast.get(0));
		model.addAttribute("pastliste",  listPast);
		
		//probe auf console nicht im web
		System.out.println("haaaaaaaaaaaaaaaaa");
		 for(String lisofPast : listPast) {
	            System.out.println(lisofPast.toString());
	        }
		
		/* List<String> listUserPast = pastRepository.getOneUsersPost(past.getAuthor());
		 model.addAttribute("oneUserPastliste",  listUserPast);
		 for(String lisofUserPast : listUserPast) {
	            System.out.println(lisofUserPast.toString());
	        }
		 System.out.println(past.getAuthor());*/
		 List<String> listPastee = pastRepository.getOneUsersPost22(past.getAuthor());
		 System.out.println("Author ist: " + past.getAuthor());
		 for(String lisofPastee : listPastee) {
	            System.out.println(lisofPastee.toString());
	        }
		 
		
		return "newPast";
	}
	
	
	/**
	 * search usernames containing the sequence of characters
	 * 
	 * @param user
	 *            User object filled in form
	 * @param model
	 * @return
	 */
	/*@RequestMapping(value = "/searchuser/{pattern}", method = RequestMethod.GET)
	public String searchUser(@PathVariable("pattern") String pattern, @ModelAttribute User user, Model model) {

		Map<String, User> retrievedUsers = userRepository.findUsersWith(pattern);

		model.addAttribute("users", retrievedUsers);
		return "users";
	}*/
	

}
