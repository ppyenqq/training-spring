package de.hska.lkit.demo.redis.controller;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import de.hska.lkit.demo.redis.model.Post;
import de.hska.lkit.demo.redis.repo.PostRepository;

@Controller
public class PostController {
	private PostRepository postRepository;

	@Autowired
	public PostController(PostRepository postRepository) {
		super();
		this.postRepository = postRepository;
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
	@RequestMapping(value = "/addpost", method = RequestMethod.GET)
	public String addPast(@ModelAttribute Post post) {
		return "newPost";
	}

	/**
	 * add a new user, adds a list of all users to model
	 * 
	 * @param user
	 *            User object filled in form
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/addpost", method = RequestMethod.POST)
	public String saveUser(@ModelAttribute Post post, Model model) {

		postRepository.savePost(post);
		model.addAttribute("message", "User successfully added");

		Map<String, Post> retrievedPast = postRepository.getAllPost();

		model.addAttribute("pasts", retrievedPast);
		
	/*	List<String> nippi = new LinkedList();
		nippi.add("hello");
		model.addAttribute("heyo random added not part of list", nippi.get(0));*/
		//checke im redis-cli was für objekte in listPast und andere wie hash set sind
		List<String> listPost = postRepository.getAllPostList();
		model.addAttribute("pastlist", listPost.get(0));
		model.addAttribute("pastliste",  listPost);
		
		//probe auf console nicht im web
		System.out.println("haaaaaaaaaaaaaaaaa");
		 for(String lisofPast : listPost) {
	            System.out.println(lisofPast.toString());
	        }
		
		
		/* List<String> listUserPast = postRepository.getOneUsersPost(post.getAuthorId());
		 model.addAttribute("oneUserPastliste",  listUserPast);
		 for(String lisofUserPast : listUserPast) {
	            System.out.println(lisofUserPast.toString());
	        }*/
		 System.out.println(post.getAuthorId());
//		 List<String> listPastee = postRepository.getOneUsersPost22(post.getAuthorId());
	
		 System.out.println("Author ist: " + post.getAuthorId());
//		 for(String lisofPastee : listPastee) {
//	            System.out.println(lisofPastee.toString());
//	        }
//		 
		
		return "newPost";
	}
	
	/*
	@RequestMapping( value="/home", method = RequestMethod.GET)
	public String pipi() {
		return "home";
	}
	
	
	@RequestMapping( value="/homes", method = RequestMethod.POST)
	public String pipii(@ModelAttribute Post post, Model model) {
		postRepository.savePost(post);
		model.addAttribute("message", "User successfully added");

		Map<String, Post> retrievedPast = postRepository.getAllPost();

		model.addAttribute("pasts", retrievedPast);
		
		List<String> nippi = new LinkedList();
		nippi.add("hello");
		model.addAttribute("heyo random added not part of list", nippi.get(0));
		//checke im redis-cli was für objekte in listPast und andere wie hash set sind
		List<String> listPost = postRepository.getAllPostList();
		model.addAttribute("pastlist", listPost.get(0));
		model.addAttribute("pastliste",  listPost);
		
		//probe auf console nicht im web
		System.out.println("haaaaaaaaaaaaaaaaa");
		 for(String lisofPast : listPost) {
	            System.out.println(lisofPast.toString());
	        }
		
		 List<String> listUserPast = pastRepository.getOneUsersPost(past.getAuthor());
		 model.addAttribute("oneUserPastliste",  listUserPast);
		 for(String lisofUserPast : listUserPast) {
	            System.out.println(lisofUserPast.toString());
	        }
		 System.out.println(past.getAuthor());
		 List<String> listPastee = postRepository.getOneUsersPost22(post.getAuthorId());
		 System.out.println("Author ist: " + post.getAuthorId());
		 for(String lisofPastee : listPastee) {
	            System.out.println(lisofPastee.toString());
	        }
		 
		return "allPost";
	}*/
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
